package psycho.mountain.tastinggenie.Utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.*

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

fun deleteFileByUri(uri: Uri, context: Context) {
    fileFromUri(uri, context)?.let{
        it.delete()

        // ContentProviderからも削除する
        context.contentResolver.delete(uri, null, null)
    }
}

fun compressBitmap(uri : Uri, context: Context) : Bitmap {
    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    return Bitmap.createScaledBitmap(bitmap, 160, 320, true)

//        val stream = contentResolver.openInputStream(uri)
//        var opts = BitmapFactory.Options()
//        opts.inJustDecodeBounds = true
//        BitmapFactory.decodeStream(stream, null, opts)
//
//        Log.d("asdf", "height: ${opts.outHeight}")
//        Log.d("asdf", "height: ${opts.outWidth}")
//        Log.d("asdf", "Size: ${stream.readBytes().size}")
//
//        // 3: RGB
//        val orgSizeMb = opts.outHeight * opts.outWidth * 3/ 10e6
//        var scale = 0
//        for (i in 1..(orgSizeMb).roundToInt()) {
//            // 1MBに抑えたい TODO: 後で可変にしても良い
//            scale = i * i
//            if (orgSizeMb / scale <= 1) {
//                break
//            }
//        }
//
//        opts = BitmapFactory.Options()
//        opts.inSampleSize = scale
//        opts.inJustDecodeBounds = false
//        stream.close()
//
//        return  BitmapFactory.decodeStream(stream, null, opts)
}
