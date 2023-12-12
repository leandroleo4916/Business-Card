package br.com.dio.businesscard.ui.utils

import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.io.FileInputStream

class EncodeString {

    fun getEncoding(arquivo: File): String? {
        val detector: UniversalDetector
        var encoding: String?
        val buf: ByteArray
        val fis: FileInputStream
        var nread: Int

        try {
            buf = ByteArray(4096)
            fis = FileInputStream(arquivo)
            detector = UniversalDetector(null)
            while (fis.read(buf).also { nread = it } > 0 && !detector.isDone) {
                detector.handleData(buf, 0, nread)
            }
            detector.dataEnd()
            encoding = detector.detectedCharset
            if (encoding == null) { encoding = "UTF-8" }
            detector.reset()
        } catch (e: Exception) { encoding = "UTF-8" }

        return encoding
    }
}