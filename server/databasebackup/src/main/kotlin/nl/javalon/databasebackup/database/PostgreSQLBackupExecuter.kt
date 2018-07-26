package nl.javalon.databasebackup.database

import java.io.File

/**
 * Creates a database dump of a PostgreSQL database. Assumes the backup command can be used without
 * any additional authentication or otherwise needed steps.
 *
 * @author Jelle Stege
 *
 * @property backupCommand The command to use to create a database dump, defaults to `pg_dump`.
 * @property database The database to create a dump for.
 */
class PostgreSQLBackupExecuter(
        private val database: String,
        private val backupCommand: String = "pg_dump"): DatabaseBackupExecuter {

    /**
     * Creates a database dump by creating a new subprocess. This subprocess calls the given
     * backup command and redirects the output to the given output File.
     *
     * @param outputFile The [File] to store the output of the backup command in.
     *
     * @return The output File again.
     * @throws IllegalStateException When the backup command did not succeed.
     */
    override fun createBackup(outputFile: File): File {
        val processBuilder = ProcessBuilder(backupCommand, database)
        processBuilder.redirectOutput(outputFile)
        val process = processBuilder.start()
        if (process.waitFor() != 0) {
            throw IllegalStateException("$backupCommand exited unexpectedly.")
        }

        return outputFile
    }
}
