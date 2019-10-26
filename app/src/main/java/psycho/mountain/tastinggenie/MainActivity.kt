package psycho.mountain.tastinggenie

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import java.io.IOException
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import org.jetbrains.anko.imageURI
import psycho.mountain.tastinggenie.database.SakeList

class MainActivity : AppCompatActivity(),
    SakeListFragment.SakeListListener,
    SakeInformationFragment.SakeInformationFragmentListener {

    val REQUEST_GET_IMAGE = 100
    private var mUri: Uri? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            //fragmentTransaction.addToBackStack(null)

            fragmentTransaction.replace(R.id.container, SakeListFragment.newInstance())
            fragmentTransaction.commit()
        }
    }

    override fun onClickAddPhotoButton(imageView: ImageView) {
        this.imageView = imageView

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

    override fun onClickAddButton() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // 一つPopしておく
        fragmentManager.popBackStack()
        // さらにPushすると，一つ前の値をPushしたことになる
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, SakeDetailedFragment.newInstance())
        fragmentTransaction.commit()
    }

    override fun onItemClick(sake: SakeList) {
        Toast.makeText(this, sake.name, Toast.LENGTH_SHORT).show()
    }

    override fun onFabButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, SakeInformationFragment.newInstance())
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // カメラ・ドキュメント画像の取得とviewへの設定
        if (requestCode == REQUEST_GET_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                // キャンセル時
                return
            }

            val resultUri : Uri? = if (data?.data != null) data.data else mUri
            resultUri?.let{
                MediaScannerConnection.scanFile(this, arrayOf(it.path) as Array<String>, arrayOf("image/jpg"), null)
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    imageView?.let {
                        it.imageURI = resultUri// imageBitmap = compressBitmap(it) // TODO
                    }
                } catch (e : IOException){
                    e.printStackTrace()
                }
            }
        }
    }

    fun compressBitmap(uri : Uri) : Bitmap {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
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

}
