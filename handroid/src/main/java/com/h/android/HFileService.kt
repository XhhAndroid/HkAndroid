package com.h.android

import android.Manifest
import android.content.Context
import android.os.Environment
import androidx.annotation.RequiresPermission
import java.io.*

/**
 *2021/9/10
 *@author zhangxiaohui
 *@describe 文件管理
 */
class HFileService private constructor() {
    /**
     * 后缀名分隔符
     */
    val EXTENSION_SEPARATOR = '.'

    /**
     * Unix路径分隔符
     */
    private val UNIX_SEPARATOR = '/'

    /**
     * Windows路径分隔符
     */
    private val WINDOWS_SEPARATOR = '\\'

    companion object {
        private var fileService: HFileService? = null

        @Synchronized
        fun get(): HFileService {
            if (fileService == null) {
                fileService = HFileService()
            }
            return fileService!!
        }
    }

    fun getExtension(uri: String?): String {
        if (uri == null) return ""
        val extensionIndex: Int = uri.lastIndexOf(EXTENSION_SEPARATOR)
        val lastUnixIndex: Int = uri.lastIndexOf(UNIX_SEPARATOR)
        val lastWindowsIndex: Int = uri.lastIndexOf(WINDOWS_SEPARATOR)
        val index = Math.max(lastUnixIndex, lastWindowsIndex)
        val value = if (index > extensionIndex || extensionIndex < 0) "" else uri.substring(extensionIndex + 1)
        return (EXTENSION_SEPARATOR + value)
    }

    /**
     * 获取私有文件
     */
    fun privateFileIsExit(fileName: String): File {
        val file = getPrivateFileDir()
        return File(file, fileName)
    }

    /**
     * 获取私有文件内容
     */
    fun getPrivateFileContent(fileName: String): String {
        val file = getPrivateFileDir()
        return readFileString(file, fileName)
    }

    fun createPrivateFile(path: String, fileName: String): File {
        val file = File(path, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        return file
    }

    /**
     * 私有空间创建一个文件
     */
    fun createPrivateFile(fileName: String): File {
        val dir = getPrivateFileDir()
        val file = File(dir, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        return file
    }

    /**
     * 公共存储空间文件内容
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun getPublicFileContent(fileName: String): String {
        val file = getPublicFileDir()
        return readFileString(file, fileName)
    }

    /**
     * 存储内容到私有文件
     * @param append 追加文件内容
     */
    fun savePrivateFile(fileName: String, content: String, append: Boolean): File {
        val file = getPrivateFileDir()
        return writeFileString(file, fileName, content, append)
    }

    /**
     * 存储内容到公共文件
     * @param append 追加文件内容
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun savePublicFile(fileName: String, content: String, append: Boolean): File {
        val file = getPublicFileDir()
        return writeFileString(file, fileName, content, append)
    }

    /**
     * 获取私有存储目录
     */
    fun getPrivateFileDir(): File {
        val file: File = HAndroid.getApplication().getDir(
            HFileService::class.java.simpleName,
            Context.MODE_PRIVATE
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * 创建私有存储文件路径
     */
    fun createPrivateFilePath(vararg filePath: String): File {
        val parentFile = getPrivateFileDir()
        val filepath: StringBuilder = StringBuilder()
        for (f in filePath) {
            if (f.isBlank()) {
                continue
            }
            filepath.append(f).append(File.separator)
        }
        val privateFile = if (filepath.isBlank()) {
            File(parentFile.absolutePath + File.separator)
        } else {
            File(parentFile.absolutePath + File.separator + filepath.toString())
        }
        if (privateFile.parentFile?.exists() != true) {
            privateFile.parentFile?.mkdirs()
        }
        if (!privateFile.exists()) {
            privateFile.mkdirs()
        }
        return privateFile
    }

    /**
     * 获取公共存储空间目录
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun getPublicFileDir(): File {
        val file = File(
            StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
                .append(File.separator)
                .append(HAndroid.getApplication().packageName)
                .append(File.separator)
                .append(HFileService::class.java.simpleName)
                .toString()
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun saveInputStreamToPrivateFile(fileName: String, inputStream: InputStream): File {
        val parentFile = getPrivateFileDir()
        val file = File(parentFile, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }

        val fileOutputStream = FileOutputStream(file)
        val bytes: ByteArray = ByteArray(1024)
        var readLength = 0
        var curLength = 0
        while (readLength != -1) {
            fileOutputStream.write(bytes, 0, readLength)
            curLength += readLength
            readLength = inputStream.read(bytes)
        }
        inputStream.close()
        fileOutputStream.close()
        return file
    }

    /**
     * 读取assets下文件内容
     */
    fun openAssetsFile(fileName: String): String {
        val inputStream = HAndroid.getApplication().applicationContext.resources.assets
            .open(fileName)
        val bf = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        val stringBuilder = java.lang.StringBuilder()
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        return stringBuilder.toString()
    }

    private fun writeFileString(dir: File, fileName: String, content: String, append: Boolean): File {
        val file = File(dir, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        FileWriter(file, append).use { fw ->
            fw.write(content)
            fw.flush()
        }
        return file
    }

    private fun readFileString(dir: File, fileName: String): String {
        val file = File(dir, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        FileReader(file).use { fr ->
            var c = 0
            val sb = StringBuffer()
            while ((fr.read().also { c = it }) != -1) {
                sb.append(c.toChar())
            }
            return sb.toString()
        }
    }
}