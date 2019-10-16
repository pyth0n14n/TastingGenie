package psycho.mountain.tastinggenie

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.fragment_register_db_fragment.*
import org.jetbrains.anko.imageBitmap
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import android.os.Build
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import org.jetbrains.anko.imageURI
import java.lang.Math.pow
import java.lang.Math.round
import java.nio.file.Files.size
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), TestFragment.TestFragmentListener, RegisterDBFragment.ImageSelectListener {

    val REQUEST_GET_IMAGE = 100
    val REQUEST_CHOOSER = 1000
    val MAX_IMAGE_SIZE = 500
    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            //fragmentTransaction.addToBackStack(null)

            fragmentTransaction.replace(R.id.container, TestFragment.newInstance())
            fragmentTransaction.commit()
        }
    }

    override fun onClickTestButton() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // 戻り先がnull菜のではなく，バックスタックに遷移を記録する（戻せるようにする）という意味
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, RegisterDBFragment.newInstance())
        fragmentTransaction.commit()
    }


//    override fun onImageSelectAction() {
//        val pickPhotoIntent : Intent = Intent(Intent.ACTION_GET_CONTENT)
//        pickPhotoIntent.setType("image/*")
//
//        val photoName = System.currentTimeMillis().toString() + ".jpg"
//        val contentValues = ContentValues()
//        contentValues.put(MediaStore.Images.Media.TITLE, photoName)
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        val uri = contentResolver
//            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        val takePhotoIntent : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//
//        val chooserIntent : Intent = Intent.createChooser(pickPhotoIntent, "Picture...")
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, takePhotoIntent)
//
//        startActivityForResult(chooserIntent, REQUEST_GET_IMAGE)
//    }

    override fun onImageSelectAction() {
        showGallery()
    }

    private fun showGallery() {
        //カメラの起動Intentの用意
        val photoName = System.currentTimeMillis().toString() + ".jpg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, photoName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        mUri = contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mUri)

        // ギャラリー用のIntent作成
        val intentGallery: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intentGallery.addCategory(Intent.CATEGORY_OPENABLE)
        intentGallery.type = "image/jpeg"

        val intent = Intent.createChooser(intentCamera, "画像の選択")
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentGallery))
        startActivityForResult(intent, REQUEST_GET_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_GET_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                // キャンセル時
                return
            }

            val resultUri : Uri? = if (data?.data != null) data.data else mUri
            resultUri?.let{
                MediaScannerConnection.scanFile(this, arrayOf(it.path) as Array<String>, arrayOf("image/jpg"), null)
                try {
//                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    register_db_image.imageBitmap = compressBitmap(it) // TODO
                } catch (e : IOException){
                    e.printStackTrace()
                }
            }
        }
    }

    fun compressBitmap(uri : Uri) : Bitmap {
        val stream = contentResolver.openInputStream(uri)
        var opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, opts)

        Log.d("asdf", "height: ${opts.outHeight}")
        Log.d("asdf", "height: ${opts.outWidth}")
        Log.d("asdf", "Size: ${stream.readBytes().size}")

        // 3: RGB
        val orgSizeMb = opts.outHeight * opts.outWidth * 3/ 10e6
        var scale = 0
        for (i in 1..(orgSizeMb).roundToInt()) {
            // 1MBに抑えたい TODO: 後で可変にしても良い
            scale = i * i
            if (orgSizeMb / scale <= 1) {
                break
            }
        }

        opts = BitmapFactory.Options()
        opts.inSampleSize = scale
        opts.inJustDecodeBounds = false
        stream.close()

        return  BitmapFactory.decodeStream(stream, null, opts)
    }

}
