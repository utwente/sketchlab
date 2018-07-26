package nl.javalon.databasebackup.utils

import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.util.concurrent.ThreadLocalRandom

/**
 * File utilities functions to create temporary directories and compare two files.
 * @author Jelle Stege
 */
object Files {
    /**
     * Creates a temporary directory using the system's temporary directory settings.
     *
     * @return A temporary directory, stored in a [File]
     */
    fun createTempDirectory(): File {
        return Files.createTempDirectory("${ThreadLocalRandom.current().nextInt()}").toFile()
    }

    /**
     * Compares two files line by line.
     *
     * @param file The first file to compare.
     * @param other The second file to compare.
     * @return true if the files are equal, false if otherwise.
     */
    fun equals(file: File, other: File): Boolean {
        val reader1 = FileInputStream(file).bufferedReader()
        val reader2 = FileInputStream(other).bufferedReader()

        var line1 = reader1.readLine()
        var line2 = reader2.readLine()

        while (line1 != null && line2 != null && line1 == line2) {
            line1 = reader1.readLine()
            line2 = reader2.readLine()
        }
        // If both line1 and line2 are null, then we've reached the end of the file. This means
        // that all previous lines were equal.
        return line1 == null && line2 == null
    }
}
