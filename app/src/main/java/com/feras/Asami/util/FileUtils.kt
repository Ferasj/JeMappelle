package com.feras.Asami.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

fun generateFile(context: Context, fileName: String): File? {

    val exportDir = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) )
    if (!exportDir.exists()) {
        exportDir.mkdirs();
    }

    val csv =File(exportDir, fileName);

    csv.createNewFile()

    Log.d("CSV_FILE", csv.absolutePath.toString())

    return if (csv.exists()) {
        csv
    } else {
        null
    }
}

fun goToFileIntent(context: Context, file: File): Intent {
    val intent = Intent(Intent.ACTION_VIEW)

    val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    Log.d("CSV-FILE", contentUri.toString())

    Log.d("CSV_2", contentUri.toString())
    val mimeType = context.contentResolver.getType(contentUri)
    intent.setDataAndType(contentUri, mimeType)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    return intent
}