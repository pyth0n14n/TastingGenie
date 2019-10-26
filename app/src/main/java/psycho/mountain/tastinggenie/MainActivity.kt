package psycho.mountain.tastinggenie

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import org.jetbrains.anko.imageURI
import psycho.mountain.tastinggenie.database.SakeDBManager
import psycho.mountain.tastinggenie.database.SakeList
import java.io.*
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity(),
    SakeListFragment.SakeListListener,
    SakeInformationFragment.SakeInformationFragmentListener {

    lateinit var sakeDBManager: SakeDBManager
    val REQUEST_GET_IMAGE = 100
    private var imageUri: Uri? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sakeDBManager = SakeDBManager(applicationContext)

        if (savedInstanceState == null){
            val bundle: Bundle = Bundle()
            bundle.putParcelableArrayList("sake_list", sakeDBManager.getSakeList() as ArrayList<SakeList>)

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            //fragmentTransaction.addToBackStack(null)

            val fragment = SakeListFragment.newInstance()
            fragment.arguments = bundle
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.commit()
        }
    }

    override fun onClickAddPhotoButton(imageView: ImageView) {
        this.imageView = imageView

        //カメラの起動Intentの用意
        val photoName = System.currentTimeMillis().toString() + ".jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, photoName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            // TODO: フォルダ名を任意にしたい
            // val path = Environment.DIRECTORY_PICTURES + File.pathSeparator + "利酒魔人"
        }
        imageUri = contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d("asdf: imageUri", imageUri!!.path + photoName)

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        // ギャラリー用のIntent作成
        val intentGallery: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intentGallery.addCategory(Intent.CATEGORY_OPENABLE)
        intentGallery.type = "image/jpeg"

        val intent = Intent.createChooser(intentCamera, "画像の選択")
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentGallery))
        startActivityForResult(intent, REQUEST_GET_IMAGE)
    }

    override fun onClickAddButton(sakeList: SakeList) {
        // DBへの追加
        sakeDBManager.insertSakeListFromList(sakeList)

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
            Log.d("asdf", imageUri!!.path + imageUri!!.lastPathSegment)

            var isFromDocument = false
            var resultUri: Uri? = null
            if (data?.data != null) {
                resultUri = data.data
                isFromDocument = true
                Log.d("onActivityResult", "document")
            }else{
                resultUri = imageUri
                Log.d("onActivityResult", "camera")
            }

            resultUri?.let{
                MediaScannerConnection.scanFile(this, arrayOf(it.path) as Array<String>, arrayOf("image/jpg"), null)
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    if (isFromDocument) {
                        copyImageFromBitmap(bitmap, baseContext)
                    }
                    imageView?.let {
                        it.imageURI = resultUri// imageBitmap = compressBitmap(it) // TODO
                        it.contentDescription = resultUri.toString()  // DBにはStringとして保存する
                    }
                } catch (e : IOException){
                    e.printStackTrace()
                }
            }
        }
    }

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

    fun copyImageFromBitmap(bmp: Bitmap, context: Context) {
        val photoName = System.currentTimeMillis().toString() + ".jpg"

        val bos: BufferedOutputStream? = null

        try {
            val bos = FileOutputStream(fileFromUri(imageUri!!, context))
            val baos = ByteArrayOutputStream()

            bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            bos.write(baos.toByteArray())
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            bos?.let{
                it.close()
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
