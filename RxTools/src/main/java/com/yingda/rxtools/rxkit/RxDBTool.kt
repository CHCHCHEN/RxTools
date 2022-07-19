package com.yingda.rxtools

import android.content.Context
import java.io.*

/**
 * author: chen
 * data: 2022/7/19
 * des:
*/
object RxDBTool {
    /** 读文件buffer. */
    private const val FILE_BUFFER = 1024
    private const val TAG = "RxDBTool"

    /**
     * 数据库导出到sdcard.
     * @param context
     * @param dbName 数据库名字 例如 xx.db
     */
    @JvmStatic
    fun exportDb2Sdcard(context: Context, dbName: String) {
        val filePath = context.getDatabasePath(dbName).absolutePath
        val buffer = ByteArray(FILE_BUFFER)
        var length: Int
        val output: OutputStream
        val input: InputStream
        try {
            input = FileInputStream(File(filePath))
            output = FileOutputStream(context.externalCacheDir.toString() + File.separator + dbName)
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            output.flush()
            output.close()
            input.close()
            TLog.i(TAG, "mv success!")
        } catch (e: IOException) {
            TLog.e(TAG, e.toString())
        }
    }
}