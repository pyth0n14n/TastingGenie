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
import android.util.Log


class MainActivity : AppCompatActivity(), TestFragment.TestFragmentListener, RegisterDBFragment.ImageSelectListener {

    val REQUEST_GET_IMAGE = 100
    val REQUEST_CHOOSER = 1000
    val MAX_IMAGE_SIZE = 500

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


    override fun onImageSelectAction() {
        val pickPhotoIntent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        pickPhotoIntent.setType("image/*")

        val photoName = System.currentTimeMillis().toString() + ".jpg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, photoName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val takePhotoIntent : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        val chooserIntent : Intent = Intent.createChooser(pickPhotoIntent, "Picture...")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, takePhotoIntent)

        startActivityForResult(chooserIntent, REQUEST_GET_IMAGE)
    }

//    private fun showGallery() {
//        //カメラの起動Intentの用意
//        val photoName = System.currentTimeMillis().toString() + ".jpg"
//        val contentValues = ContentValues()
//        contentValues.put(MediaStore.Images.Media.TITLE, photoName)
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        val uri = contentResolver
//            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//
//        // ギャラリー用のIntent作成
//        val intentGallery: Intent
//        if (Build.VERSION.SDK_INT < 19) {
//            intentGallery = Intent(Intent.ACTION_GET_CONTENT)
//            intentGallery.type = "image/*"
//        } else {
//            intentGallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intentGallery.addCategory(Intent.CATEGORY_OPENABLE)
//            intentGallery.type = "image/jpeg"
//        }
//        val intent = Intent.createChooser(intentCamera, "画像の選択")
//        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentGallery))
//        startActivityForResult(intent, REQUEST_GET_IMAGE)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_GET_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            try {
                Log.d("asdf", "activityResult")
                if (data.extras != null && data.extras.get("data") != null) {
                    Log.d("asdf", "bitmap")
                    val capturedImage: Bitmap = compressBitmap(data.extras.get("data") as Bitmap)
                    register_db_image.imageBitmap = capturedImage
                } else {
                    Log.d("asdf", "stream")
                    val stream: InputStream = contentResolver.openInputStream(data.data)
                    val bitmap: Bitmap = compressBitmap(BitmapFactory.decodeStream(stream))
                    stream.close()
                    register_db_image.imageBitmap = bitmap
                }
            } catch (e : FileNotFoundException) {
                e.printStackTrace()
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }

    fun compressBitmap(bitmap : Bitmap) : Bitmap {
        val WID = 360
        val HEI = 640
        val imageOptions = BitmapFactory.Options()
        return  Bitmap.createScaledBitmap(bitmap, WID, HEI, true)
    }

}
