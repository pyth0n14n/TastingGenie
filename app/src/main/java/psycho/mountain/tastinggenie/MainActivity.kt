package psycho.mountain.tastinggenie

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import org.jetbrains.anko.imageURI
import psycho.mountain.tastinggenie.database.SakeDBManager
import psycho.mountain.tastinggenie.database.SakeList
import java.io.*
import psycho.mountain.tastinggenie.utility.copyImageFromBitmap


class MainActivity : AppCompatActivity(),
    SakeListFragment.SakeListListener,
    SakeInformationFragment.SakeInformationFragmentListener,
    SakeDetailedFragment.SakeDetailedFragmentListener{

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

    // -----------------------------------------------------------------------
    // ---         ボタンの処理（画面遷移とDB操作）
    // -----------------------------------------------------------------------
    // ============== Information Fragment =================
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
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()


        // DBへの追加かDBの更新かを判断
        if (sakeList.id == -1) {
            // DBへの追加
            Log.d("MainActivity", "DB Add")
            sakeDBManager.insertSakeListFromList(sakeList)
        } else {
            // DBの更新
            Log.d("MainActivity", "DB renew")
            if (sakeDBManager.isExistSakeList(sakeList.id)) {
                Log.d("MainActivity", "DB renew exec")
                sakeDBManager.replaceSakeListFromList(sakeList)
            }
            fragmentManager.popBackStack() // 前回のDetailedFragmentへの遷移を消しておく
        }


        // 一つPopしておく
        fragmentManager.popBackStack()
        // さらにPushすると，一つ前の値をPushしたことになる
        fragmentTransaction.addToBackStack(null)

        val bundle: Bundle = Bundle()
        bundle.putParcelable("sake", sakeList)

        val fragment = SakeDetailedFragment.newInstance()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    // ============== Sake List Fragment =================
    override fun onItemClick(sake: SakeList) {
        val bundle: Bundle = Bundle()
        bundle.putParcelable("sake", sake)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        val fragment = SakeDetailedFragment.newInstance()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }


    override fun onItemLongClick(sake: SakeList) {
        // 消すかどうかの確認はすでにとってある
        sakeDBManager.deleteSakeList(sake.id)
    }

    override fun onListViewCreated(): MutableList<SakeList>? {
        return sakeDBManager.getSakeList() as MutableList<SakeList>
    }

    override fun onFabButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, SakeInformationFragment.newInstance())
        fragmentTransaction.commit()
    }

    override fun onClickEditInformation(sake: SakeList) {
        val bundle: Bundle = Bundle()
        bundle.putParcelable("sake", sake)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        val fragment = SakeInformationFragment.newInstance()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    // -----------------------------------------------------------------------
    // ---         Intent遷移の処理（カメラやドキュメントの使用）
    // -----------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // カメラ・ドキュメント画像の取得とviewへの設定
        if (requestCode == REQUEST_GET_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                // キャンセル時
                return
            }

            // カメラかドキュメントかの判断
            var isFromDocument = false
            var resultUri: Uri? = null
            if (data?.data != null) {
                resultUri = data.data
                isFromDocument = true
            }else{
                resultUri = imageUri
            }

            // 画像のセットと必要に応じて画像コピーの処理
            resultUri?.let{
                MediaScannerConnection.scanFile(this, arrayOf(it.path) as Array<String>, arrayOf("image/jpg"), null)
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    // ドキュメントの場合，アプリのストレージにコピーを作成
                    if (isFromDocument) {
                        copyImageFromBitmap(bitmap, imageUri!!, baseContext)
                    }
                    // 画像の貼り付けとURIの保持
                    imageView?.apply {
                        imageURI = resultUri// imageBitmap = compressBitmap(it) // TODO
                        contentDescription = resultUri.toString()  // DBにはStringとして保存する
                    }
                } catch (e : IOException){
                    e.printStackTrace()
                }
            }
        }
    }


}
