package nl.javalon.databasebackup

import mu.KotlinLogging
import nl.javalon.databasebackup.database.DatabaseBackupExecuter
import nl.javalon.databasebackup.utils.Files
import nl.javalon.databasebackup.utils.Gzip
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Creates and manages backups for the given database. Will first create a backup for the given
 * database. Manages the backups by making sure not too much disk space is used by removing
 * obsolete databases. It does this by using the following scheme:
 *
 * - For the past 2 hours: all backups are kept.
 * - For the past 3 days: One backup per hour is kept.
 * - For the past 90 days: One backup per day is kept.
 * - Older than 90 days: Deemed obsolute, so remove everything.
 *
 * Furthermore, only stores backups as seperate files if there are actual changes in the backup,
 * otherwise, it renames the old backup to the new backup.
 * @author Jelle Stege
 *
 * @property database The name of the database to backup and manage the stored backups for.
 * @property backupDirectory The directory in which the backups are stored.
 * @property databaseBackupExecuter The executer to use to create a database dump.
 */
class BackupManager(
        private val database: String,
        private val backupDirectory: File,
        private val databaseBackupExecuter: DatabaseBackupExecuter) {
    /**
     * String format for the backup filename.
     */
    private val backupFileFormat = "backup_${database}_%s_%s_%s.sql"

    /**
     * Regular expression to match backup files.
     */
    private val backupFileRegex = "^backup_${database}_(\\d{6})_(\\d{6})_(\\d+)\\.sql.gz$".toRegex()

    /**
     * Logging utility
     */
    private val log = KotlinLogging.logger { }

    init {
        log.info { "Initiating backup manager for database $database." }
    }

    /**
     * Executes the manager. First creates a backup file, then cleans up old files.
     */
    fun execute() {
        log.info { "Start managing backups for database \"$database\"" }

        // We need a temporary directory to store the temporary database dumps in. This is easier
        // so there can be comparison without interference of other threads and such.
        val tempDirectory = Files.createTempDirectory()
        log.info { "Created temporary directory ${tempDirectory.absolutePath}" }

        // Create a new SQL dump
        val currentBackupFile = buildDbDump(tempDirectory)

        // Retrieve the most recent backup file.
        val mostRecentBackup = readBackupFilesSorted(backupDirectory).lastOrNull()
        if (mostRecentBackup == null) {
            // If null, there is no recent backup file. In this case, just store the backup.
            log.info { "Storing ${currentBackupFile.name} as there are no older backups." }
            Gzip.zip(currentBackupFile, backupDirectory)
        } else {
            // There is a recent backup file, unzip this and compare it to the newly generated dump.
            log.info {
                "Unzipping ${mostRecentBackup.name} to " +
                        "${tempDirectory.absolutePath}/${mostRecentBackup.name.substringBeforeLast('.')}"
            }
            val unzippedRecentBackup = Gzip.unzip(mostRecentBackup, tempDirectory)

            // Compare the two files. If the files are equal, rename the old file to the new file
            // and be done with it. Otherwise, there are changes, thus store the newly generated
            // dump as a seperate file.
            if (Files.equals(currentBackupFile, unzippedRecentBackup)) {
                log.info { "Updating name of ${mostRecentBackup.name} since there are no changes." }
                val to = File(backupDirectory, currentBackupFile.name + ".gz")

                // Move the file
                val moveSucceeded = mostRecentBackup.renameTo(to)
                if (!moveSucceeded) {
                    log.error {
                        "Could not move ${mostRecentBackup.absolutePath} to ${to.absolutePath}"
                    }
                } else {
                    log.info {
                        "File moved from ${mostRecentBackup.absolutePath} " +
                                "to ${to.absolutePath}"
                    }
                }
            } else {
                log.info { "Storing ${currentBackupFile.name} as there are recent changes." }
                log.info {
                    "Zipping ${currentBackupFile.name} to " +
                            "${backupDirectory.absolutePath}/${currentBackupFile.name}.gz"
                }
                Gzip.zip(currentBackupFile, backupDirectory)
            }
        }

        // Now there is only cleanup up the old backups left.
        log.info { "Performing cleanup of old backups." }
        cleanUpBackups(backupDirectory)

        // Delete all files in the temporary directory, and after that delete itself.
        tempDirectory.deleteRecursively()
    }

    /**
     * Walk through the given directory and select only those files matching the backup file format.
     *
     * @param directory The directory to walk through.
     * @return A [Sequence] of [File]s, matching the backup files for this database, sorted on
     * creation date.
     */
    private fun readBackupFilesSorted(directory: File): Sequence<File> {
        return directory.walk()
                .filter { it.name.matches(backupFileRegex) }
                .sortedBy { parseTimeInfoFromFile(it, TimeInfo.TIMESTAMP) }
    }

    /**
     * Executes the procedure to create a database dump from the given database.
     *
     * @param outputDirectory The directory to store the database dump in.
     * @return The newly generated database dump in a [File], stored in the given outputDirectory.
     */
    private fun buildDbDump(outputDirectory: File): File {
        val filename = createFilename()
        val file = File(outputDirectory, filename)

        log.info { "Creating SQL dump for $database in $filename" }
        databaseBackupExecuter.createBackup(file)
        log.info { "SQL dump $filename created." }

        return file
    }

    /**
     * Creates a file name according to the default backup file name format. Uses the current time
     * to fill in the blanks.
     *
     * @return A filename corresponding to the backup file name format.
     */
    private fun createFilename(): String {
        val now = System.currentTimeMillis()
        val date = SimpleDateFormat("yyMMdd").format(Date(now))
        val time = SimpleDateFormat("HHmmss").format(Date(now))
        return String.format(backupFileFormat, date, time, now)
    }

    /**
     * Cleans up all backups in the given backup directory. Does this by the following rules:
     *
     * - For the past 2 hours: all backups are kept.
     * - For the past 3 days: One backup per hour is kept.
     * - For the past 90 days: One backup per day is kept.
     * - Older than 90 days: Deemed obsolute, so remove everything.
     *
     * @param backupDirectory The directory to clean up.
     */
    private fun cleanUpBackups(backupDirectory: File) {
        val now = System.currentTimeMillis()
        val backups = readBackupFilesSorted(backupDirectory).toList()
        val toRemove = mutableListOf<File>()

        var lastHour = -1L
        for (backup in backups) {
            // Get the timestamp of the backup.
            val backupTimestamp = parseTimeInfoFromFile(backup, TimeInfo.TIMESTAMP)

            if (now - backupTimestamp < TWO_HOURS) {
                // Backup is less than 2 hours old, keep it and do nothing else.
                log.info { "Keep ${backup.name}, less than 2 hours old." }
            } else if (now - backupTimestamp < ONE_DAY) { //24 Hours
                // Backup is less than 1 day old. Only one backup per hour should be kept.
                val hour = parseTimeInfoFromFile(backup, TimeInfo.HOUR)
                if (hour != lastHour) {
                    // There is no backup to be kept for this hour yet, thus keep it.
                    lastHour = hour
                    log.info {
                        "Keep ${backup.name}, " +
                                "hourly backup for backups less than 24 hours old."
                    }
                } else {
                    // There is already a backup for this hour, this backup can be removed as it
                    // is deemed obsolete.
                    log.info {
                        "Backup ${backup.name} is no longer necessary since there already " +
                                "is a backup for this hour."
                    }
                    toRemove += backup
                }
            } else {
                // Backup is older than 1 day, purge it.
                log.info { "Backup ${backup.name} is older than 1 day, marked obsolete." }
                toRemove += backup
            }
        }

        toRemove.asSequence()
                .onEach {
                    log.info { "Removing obsolete backup ${it.name}" }
                }
                .forEach {
                    if (!it.delete()) {
                        log.error { "Can not remove ${it.name} due to unknown reasons." }
                    }
                }
    }

    /**
     * Parses time information from the given [File]. The filename should adhere to the default
     * backup filename format, otherwise, no information can be parsed.
     *
     * @param file The file to parse the filename of.
     * @param timeInfo The information required of the filename.
     *
     * @return The requested information, as a [Long].
     */
    private fun parseTimeInfoFromFile(file: File, timeInfo: TimeInfo): Long {
        val matches = backupFileRegex.matchEntire(file.name)
                ?: throw IllegalArgumentException("Argument $file does not adhere to backup file" +
                "format.")

        // The first value represents the matched string, thus the filename. We don't need this.
        val (_, date, time, timestamp) = matches.groupValues

        return when (timeInfo) {
        //Year is in yyMMdd format
            TimeInfo.YEAR -> date.substring(0, 2)
            TimeInfo.MONTH -> date.substring(2, 4)
            TimeInfo.DAY -> date.substring(4, 6)
        //Time is in HHmmss format
            TimeInfo.HOUR -> time.substring(0, 2)
            TimeInfo.MINUTE -> time.substring(2, 4)
            TimeInfo.SECOND -> time.substring(4, 6)
        //Timestamp is just a timestamp
            TimeInfo.TIMESTAMP -> timestamp
        }.toLong()
    }

    /**
     * Enum representing the information to be requested of a filename.
     */
    private enum class TimeInfo {
        YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, TIMESTAMP
    }
}

/**
 * Two hours in milliseconds.
 */
private const val TWO_HOURS = 7200_000

/**
 * One day in milliseconds.
 */
private const val ONE_DAY = 86400_000
/**
 * Three days in milliseconds.
 */
private const val THREE_DAYS = 259200_000
/**
 * 90 days in milliseconds.
 */
private const val NINETY_DAYS = 7776000_000

