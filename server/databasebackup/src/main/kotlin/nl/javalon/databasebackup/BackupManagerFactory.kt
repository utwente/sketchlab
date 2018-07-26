package nl.javalon.databasebackup

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import nl.javalon.databasebackup.database.PostgreSQLBackupExecuter
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.concurrent.thread

/**
 * Factory creating [BackupManager]s. This factory creates managers for each passed on database.
 * @author Jelle Stege
 */
class BackupManagerFactory(parser: ArgParser) {
    /**
     * Logging utility
     */
    val log = KotlinLogging.logger { }

    /**
     * The database names to backup.
     */
    val databases: List<String> by parser.adding(
            "-d", "--database",
            help = "A database to backup, can be repeated."
    ).default(emptyList<String>())

    /**
     * The directory to store and manage backups in.
     */
    val backupFolder by parser.storing(
            "-b", "--backupDirectory",
            help = "Directory to store backups in. This directory will also be altered upon" +
                    "the creation of a new backup."
    ) { File(this) }
}

/**
 * Runs the DatabaseManager utility. For a list of command line flags use the --help option.
 * @param rawArgs The raw arguments passed to this program.
 */
fun main(rawArgs: Array<String>) = mainBody("Backup Manager Utility") {
    BackupManagerFactory(ArgParser(rawArgs, helpFormatter = DefaultHelpFormatter())).run {
        if (!backupFolder.exists() || !backupFolder.isDirectory) {
            throw IllegalArgumentException("Given backup directory does not exist.")
        }
        this.databases
                .asSequence()
                .onEach {
                    log.info { "Starting backup program for database \"$it\"..." }
                }
                //Create a "thread ID" for each database. This makes it easier to grep the log.
                .zip((ThreadLocalRandom.current().nextInt(20000)..Int.MAX_VALUE).asSequence())
                .map { (it, threadId) ->
                    //Create a thread for all databases.
                    thread(start = false, name = "$threadId-$it") {
                        val d = BackupManager(it, backupFolder, PostgreSQLBackupExecuter(it))
                        d.execute()
                    }
                }
                .forEach { it.start() }
    }
}
