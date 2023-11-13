package kr.co.jiniaslog.blog.domain.article

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object CompressionUtils {
    @Throws(IOException::class)
    fun compressString(data: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { gzipOutputStream ->
            gzipOutputStream.write(data.toByteArray(StandardCharsets.UTF_8))
        }
        return byteArrayOutputStream.toByteArray()
    }

    @Throws(IOException::class)
    fun decompressString(compressedData: ByteArray?): String {
        val byteArrayInputStream = ByteArrayInputStream(compressedData)
        val stringBuilder = StringBuilder()
        GZIPInputStream(byteArrayInputStream).use { gzipInputStream ->
            val reader: Reader = InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(reader)
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        }
        return stringBuilder.toString()
    }
}
