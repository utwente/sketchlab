package nl.javalon.databasebackup.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * GZip utilities that zip and unzip a file.
 * @author Jelle Stege
 */
object Gzip {

    /**
     * Zips a file using GZip compression.
     *
     * @param file The file to zip
     * @param outputDirectory The directory in which to store the zipped file.
     * @return The zipped file, name is equal to the given filename, plus the .gz extension.
     */
    fun zip(file: File, outputDirectory: File): File {

        val zippedFile = File(outputDirectory, "${file.name}.gz")

        val zipper = GZIPOutputStream(FileOutputStream(zippedFile))
        val toZipStream = FileInputStream(file)

        toZipStream.copyTo(zipper)
        toZipStream.close()
        zipper.finish()
        zipper.close()

        return zippedFile
    }

    /**
     * Unzips a file using GZip decompression.
     *
     * @param file The file to unzip
     * @param outputDirectory The directory in which to store the unzipped file.
     * @return The unzipped file, name is equal to the zipped filename, without the .gz extension.
     */
    fun unzip(file: File, outputDirectory: File): File {

        val unzippedFile = File(outputDirectory, file.name.substringBeforeLast('.'))

        val unzipper = GZIPInputStream(FileInputStream(file))
        val toUnzipStream = FileOutputStream(unzippedFile)

        unzipper.copyTo(toUnzipStream)
        unzipper.close()
        toUnzipStream.close()

        return unzippedFile
    }
}
