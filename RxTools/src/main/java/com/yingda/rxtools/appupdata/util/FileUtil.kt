package com.yingda.rxtools.appupdate.util

import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

/**
 * author: chen
 * data: 2024/8/7
 * des:
*/

class FileUtil {
    companion object {
        fun createDirDirectory(path: String) {
            File(path).let {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
        }

        fun md5(file: File): String {
            try {
                val buffer = ByteArray(1024)
                var len: Int
                val digest = MessageDigest.getInstance("MD5")
                val inStream = FileInputStream(file)
                while (inStream.read(buffer).also { len = it } != -1) {
                    digest.update(buffer, 0, len)
                }
                inStream.close()
                val bigInt = BigInteger(1, digest.digest())
                return bigInt.toString(16).padStart(32, '0').uppercase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    }
}