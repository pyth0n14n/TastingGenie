package psycho.mountain.tastinggenie

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.ContentValues
import android.content.DialogInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.dialog_dont_show_me.*
import kotlinx.android.synthetic.main.dialog_dont_show_me.view.*
import org.jetbrains.anko.imageURI
import psycho.mountain.tastinggenie.database.SakeDBManager
import psycho.mountain.tastinggenie.database.SakeList
import psycho.mountain.tastinggenie.database.SakeReview
import psycho.mountain.tastinggenie.utility.*
import java.io.*


class MainActivity : AppCompatActivity(),
    SakeListFragment.SakeListListener,
    SakeInformationFragment.SakeInformationFragmentListener,
    SakeDetailedFragment.SakeDetailedFragmentListener,
    SakeReviewFragment.SakeReviewFragmentListener{

    lateinit var sakeDBManager: SakeDBManager
    val REQUEST_GET_IMAGE = 100
    private var imageUri: Uri? = null
    private var imageView: ImageView? = null

    // DBアクセスの回数を減らすために，initで取得し，更新時情報だけを反映する．
    // 変更可能なListであるMutableListとして保持する
    private var sakeLists: MutableList<SakeList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sakeDBManager = SakeDBManager(applicationContext)
        sakeLists = sakeDBManager.getSakeList() as MutableList<SakeList>

        if (savedInstanceState == null){
            val bundle: Bundle = Bundle()
            // Listは参照渡しされる．Fragmentで変更するとActivity側も変わるので注意．
            bundle.putParcelableArrayList("sake_list", sakeLists as ArrayList<SakeList>)

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

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
            // TODO: これは生のPicturesディレクトリ．パスを追加してあげれば何とかなりそう．
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
            val id = sakeDBManager.insertSakeListFromList(sakeList)
            // 新規登録して自動で割り振られたIDに振り変える
            sakeList.id = id

            // MainActivityのメンバ変数の更新
            sakeLists?.let{
                it.add(sakeList)
            }
        } else {
            // DBの更新
            Log.d("MainActivity", "DB renew")
            // TODO: これってバグでIDを渡し損ねない限り，存在しないはずがないと思うのだけど．．？
            if (sakeDBManager.isExistSakeList(sakeList.id)) {
                Log.d("MainActivity", "DB renew exec")
                sakeDBManager.replaceSakeListFromList(sakeList)
            }
            fragmentManager.popBackStack() // 前回のDetailedFragmentへの遷移を消しておく

            // MainActivityのメンバ変数の更新
            sakeLists?.let {
                // 二つ以上はあり得ないことになっている; TODO: 一応，例外処理を
                val oldSake = it.filter{ it.id == sakeList.id }[0]
                it.remove(oldSake)
                it.add(sakeList)
            }
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
        Log.d("MainActivity", sake.id.toString() + ": " + sake.name + " is removed")
        // 消すかどうかの確認はすでにとってある
        sakeDBManager.deleteSakeList(sake.id)
        sakeDBManager.deleteSakeReviewBySakeListId(sake.id)

        // こいつの削除は，fragmentに任せる
//        sakeLists?.let{
//            Log.d("MainActivity", "Before Remove")
//            for (i in 0 until it.size) {
//                Log.d("MainActivity", it[i].id.toString() + ": " + it[i].name)
//            }
//
//            val success = it.remove(sake)
//            Log.d("MainActivity", "sakeList variable remove succeeded? {$success}")
//            Log.d("MainActivity", "After Remove")
//            for (i in 0 until it.size) {
//                Log.d("MainActivity", it[i].id.toString() + ": " + it[i].name)
//            }
//        }
    }

    override fun onFabButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, SakeInformationFragment.newInstance())
        fragmentTransaction.commit()
    }

    // ============== Sake Detailed Fragment =================
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

    override fun onFabReviewButtonClick(id: Int) {
        val bundle = Bundle()
        bundle.putInt("sake_list_id", id)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        val fragment = SakeReviewFragment.newInstance()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onReviewItemClick(sakeReview: SakeReview) {
        val bundle: Bundle = Bundle()
        bundle.putParcelable("sakeReview", sakeReview)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        val fragment = SakeReviewFragment.newInstance()
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onReviewItemLongClick(sakeReview: SakeReview) {
        sakeDBManager.deleteSakeReview(sakeReview.review_id)
    }

    override fun onSakeReviewListCreated(sake_list_id: Int): MutableList<SakeReview>? {
        return sakeDBManager.getSakeReviewBySakeListId(sake_list_id) as MutableList<SakeReview>
    }

    override fun onClickReviewAddButton(sakeReview: SakeReview) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // DBへの追加かDBの更新かを判断
        if (sakeReview.review_id == -1) {
            // DBへの追加
            Log.d("MainActivity", "DB Review Add")
            sakeDBManager.insertSakeReviewFromList(sakeReview)
        } else {
            // DBの更新
            Log.d("MainActivity", "DB Review Renew")
            sakeDBManager.replaceSakeReviewFromList(sakeReview)
        }

        // 二つPopしておく
        fragmentManager.popBackStack()  // review fragment
        fragmentManager.popBackStack()  // detailed fragment
        // さらにPushすると，一つ前の値をPushしたことになる
        fragmentTransaction.addToBackStack(null)  // detailed fragment

        val bundle: Bundle = Bundle()
        Log.d("return to Detailed", "sakeListId: " + sakeReview.id.toString())
        val sakeList = sakeDBManager.getSakeListById(sakeReview.id)
        bundle.putParcelable("sake", sakeList)

        val fragment = SakeDetailedFragment.newInstance()
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
                    val storeUncompressed: Boolean = needStoreUncompressed()

                    if (isFromDocument) {
                        copyImageFromUri(it, imageUri!!, storeUncompressed, baseContext) // TODO: applicationContextとの違いは？
                    }
                    else {
                        copyImageFromUri(it, it, storeUncompressed, baseContext)
                    }

                    // 画像の貼り付けとURIの保持
                    imageView?.apply {
                        imageURI = imageUri
                        contentDescription = imageUri.toString()  // DBにはStringとして保存する
                    }
                } catch (e : IOException){
                    e.printStackTrace()
                }
            }
        }
    }

    // 無圧縮画像を保存するかの確認
    private fun needStoreUncompressed(): Boolean {
        var storeUncompressed: Boolean = true
        if (isStoreUncompressedConfirmed(this)) {
            val doNotShowMeView = View.inflate(this, R.layout.dialog_dont_show_me, null)
            AlertDialog.Builder(this).apply {
                setView(doNotShowMeView)
                setTitle("無圧縮画像を保存しますか？")
                setMessage("メモリあふれ防止のために，画像は圧縮した状態で保存されます．")
                setNegativeButton("いいえ", DialogInterface.OnClickListener { _, _ ->
                    storeUncompressed = false
                })
                setPositiveButton("はい", DialogInterface.OnClickListener {_, _ ->
                    storeUncompressed = true
                })
                show()
            }
            // 二度と表示しない処理
            if (doNotShowMeView.dont_show_me_checkbox.isChecked) {
                setStoreUncompressedConfirmed(this, false)
                setStoreUncompressed(this, storeUncompressed)
            }
        }
        else {
            storeUncompressed = isStoreUncompressed(this)
        }
        return storeUncompressed
    }


}
