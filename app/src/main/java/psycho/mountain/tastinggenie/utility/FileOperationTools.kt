package psycho.mountain.tastinggenie.utility


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.*
import android.util.DisplayMetrics
import android.graphics.Matrix
import org.jetbrains.anko.windowManager
import android.content.ContentValues




fun fileFromUri(uri: Uri, context: Context) : File? {
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)

    var file: File? = null

    cursor?.let {
        var path: String? = null
        if (it.moveToFirst()) {
            path = it.getString(0)
        }
        it.close()
        if (path != null) {
            file = File(path)
        }
    }
    return file
}

fun fileFromUncompressedUri(uri: Uri, context: Context) : File? {
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)

    var file: File? = null

    cursor?.let {
        var path: String? = null
        if (it.moveToFirst()) {
            path = it.getString(0)
        }
        it.close()
        if (path != null) {
            val splitPath = path.split("/") as MutableList<String>
            // パスの間に "uncompressed" を入れてフォルダを掘る
            splitPath.add(splitPath.lastIndex, "uncompressed")
            path = splitPath.joinToString("/")  // 再度パスのフォーマットに変換
            Log.d("join", path.toString())
            file = File(path)
        }
    }
    return file
}


fun copyImageFromBitmap(bmp: Bitmap, dstUri: Uri, context: Context) {
    val file = fileFromUri(dstUri!!, context)
    Log.d("asdf", file.toString())

    val bos = FileOutputStream(file)
    try {
        val baos = ByteArrayOutputStream()

        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        bos.write(baos.toByteArray())
        bos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        bos.close()
    }
}

fun copyImageFromUri(srcUri: Uri, dstUri: Uri, needOriginal: Boolean ,context: Context) {
    val srcFile = fileFromUri(srcUri, context)
    val dstFile = fileFromUri(dstUri, context)

    if (needOriginal) {
        fileFromUncompressedUri(dstUri, context)?.let {
            if (srcFile != null) {
                srcFile.copyTo(it, true)
                registerContentProvider(it, context)
            }
        }
    }

    var bos : FileOutputStream? = null
    try {
        val baos = ByteArrayOutputStream()

        val originalBitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, srcUri)
        val bmp = resizeBitmapToDisplaySize(originalBitmap, context)
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        bos = FileOutputStream(dstFile)
        bos.write(baos.toByteArray())
        bos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        bos?.let{
            bos.close()
        }
    }
}

fun registerContentProvider(file: File, context: Context) {
    val contentValues = ContentValues()
    val contentResolver = context.contentResolver
    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    contentValues.put("_data", file.absolutePath)
    contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}

fun resizeBitmapToDisplaySize(src: Bitmap, context: Context): Bitmap {
    val TAG = "resizeBitmap"
    val srcWidth = src.width // 元画像のwidth
    val srcHeight = src.height // 元画像のheight
    Log.d(
        TAG, "srcWidth = " + srcWidth.toString()
                + " px, srcHeight = " + srcHeight.toString() + " px"
    )

    // 画面サイズを取得する
    val matrix = Matrix()
    val metrics = DisplayMetrics()
    context.windowManager.defaultDisplay.getMetrics(metrics)
    val screenWidth = metrics.widthPixels.toFloat()
    val screenHeight = metrics.heightPixels.toFloat()
    Log.d(
        TAG, "screenWidth = " + screenWidth.toString()
                + " px, screenHeight = " + screenHeight.toString() + " px"
    )

    val widthScale = screenWidth / srcWidth
    val heightScale = screenHeight / srcHeight
    Log.d(
        TAG, "widthScale = " + widthScale.toString()
                + ", heightScale = " + heightScale.toString()
    )
    if (widthScale > heightScale) {
        matrix.postScale(heightScale, heightScale)
    } else {
        matrix.postScale(widthScale, widthScale)
    }
    // リサイズ
    val dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true)
    val dstWidth = dst.width // 変更後画像のwidth
    val dstHeight = dst.height // 変更後画像のheight
    Log.d(
        TAG, "dstWidth = " + dstWidth.toString()
                + " px, dstHeight = " + dstHeight.toString() + " px"
    )
    return dst
}

fun deleteFileByUri(uri: Uri, context: Context) {
    fileFromUri(uri, context)?.let{
        it.delete()

        // ContentProviderからも削除する
        context.contentResolver.delete(uri, null, null)
    }
}
