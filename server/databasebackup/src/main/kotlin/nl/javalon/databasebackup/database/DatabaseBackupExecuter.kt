package nl.javalon.databasebackup.database

import java.io.File

/**
 * Executer which creates a backup of a database.
 *
 * @author Jelle Stege
 */
interface DatabaseBackupExecuter {
    /**
     * Creates a backup and stores it in the given output file.
     *
     * @param outputFile The [File] in which to store the output in.
     * @return The output file.
     */
    fun createBackup(outputFile: File): File
}
