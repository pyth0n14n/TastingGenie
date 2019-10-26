package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_sake_information.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import psycho.mountain.tastinggenie.Utility.deleteFileByUri
import psycho.mountain.tastinggenie.Utility.fileFromUri
import psycho.mountain.tastinggenie.database.SakeList
import java.lang.NullPointerException
import java.lang.NumberFormatException

class SakeInformationFragment : Fragment() {

    private var sakeList: SakeList? = null  // データ更新時は既存の値をBundleで渡される
    private var listener: SakeInformationFragmentListener? = null

    interface SakeInformationFragmentListener {
        fun onClickAddButton(sakeList: SakeList)
        fun onClickAddPhotoButton(imageView: ImageView)
    }

    companion object {
        fun newInstance() : SakeInformationFragment{
            return SakeInformationFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            sakeList = it.getParcelable("sake") as SakeList
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SakeInformationFragmentListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sake_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sakeList?.let {
            setupView(it)
        }

        context?.let {
            sake_information_grade.setOnClickListener {
                dialogSakeGrade(context!!)
            }
            sake_information_type.setOnClickListener {
                dialogSakeType(context!!)
            }
            sake_information_prefecture.setOnClickListener {
                dialogSakePrefecture(context!!)
            }

            // 甘辛を自動で入力
            sake_information_sake_deg.onFocusChange { _, _ ->
                try {
                    val deg = sake_information_sake_deg.text.toString().toFloat()
                    sake_information_sake_deg_level.text = degToSweetLevel(deg)
                }
                catch(e: NumberFormatException) {
                    e.printStackTrace()
                    sake_information_sake_deg_level.text = ""
                }
            }

            // DBに追加する処理
            button_add_sake_list.setOnClickListener {
                // 入力チェックOKならば DB 操作して状態遷移
                if (validateSakeList()) {
                    val sakeList = makeSakeList()
                    listener?.onClickAddButton(sakeList)
                }
            }

            // 写真追加処理
            button_add_sake_photo.setOnClickListener{
                listener?.onClickAddPhotoButton(sake_information_image)
            }

            // 長押しで写真削除
            val dialog = AlertDialog.Builder(it)
            sake_information_image.setOnLongClickListener {
                dialog.apply {
                    setTitle("画像の削除")
                    setMessage("本当に消しますか？")
                    setPositiveButton("はい", DialogInterface.OnClickListener { _, _ ->
                        sake_information_image.setImageResource(R.drawable.empty_image)
                        val uri = Uri.parse(sake_information_image.contentDescription.toString())
                        deleteFileByUri(uri, context)
                    })
                    setNegativeButton("いいえ", DialogInterface.OnClickListener{_, _ -> })
                    show()
                }
                true

            }
        }
    }

    private fun setupView(sake: SakeList) {
        if (sake.image != "") {
            sake_information_image.setImageURI(Uri.parse(sake.image))
            sake_information_image.contentDescription = sake.image
        }
        if (sake.name != "") {sake_information_name.setText(sake.name)}
        if (sake.grade != "") {sake_information_grade.text = sake.grade}
        if (sake.type != "") {sake_information_type.text = sake.type}
        if (sake.maker != "") {sake_information_maker.setText((sake.maker))}
        if (sake.prefecture != "") {sake_information_prefecture.text = sake.prefecture}
        if (sake.sake_deg >= 0.0F) {
            sake_information_sake_deg.setText(sake.sake_deg.toString())
            sake_information_sake_deg_level.text = degToSweetLevel(sake.sake_deg)
        }
        if (sake.pol_rate >= 0) {sake_information_pol_rate.setText(sake.pol_rate.toString())}
        if (sake.alcohol >= 0) {sake_information_alcohol.setText(sake.alcohol.toString())}
        if (sake.rice != "") {sake_information_rice.setText(sake.rice)}
        if (sake.yeast != "") {sake_information_yeast.setText(sake.yeast)}
        button_add_sake_list.text = "更新"
    }

    private fun makeSakeList(): SakeList {
        return SakeList(-1,// IDはダミー．DBに自動入力して貰う
            viewToString(sake_information_name),
            viewToString(sake_information_grade),
            viewToString(sake_information_type),
            if (sake_information_image.contentDescription == null) ""
            else sake_information_image.contentDescription.toString(),
            viewToString(sake_information_maker),
            viewToString(sake_information_prefecture),
            viewToFloat(sake_information_sake_deg),
            viewToInt(sake_information_pol_rate),
            viewToInt(sake_information_alcohol),
            viewToString(sake_information_rice),
            viewToString(sake_information_yeast)
        )
    }

    private fun viewToString(textView: TextView): String{
        lateinit var data: String
        try {
            data = textView.text.toString()
        } catch(e: NullPointerException) {
            data = ""
        }
        return data
    }

    private fun viewToFloat(textView: TextView): Float{
        var data: Float = 0.0F
        try {
            data = textView.text.toString().toFloat()
        } catch(e: NullPointerException) {
            data = -1.0F
        } catch(e: NumberFormatException) {
            data = -2.0F
        }
        return data
    }

    private fun viewToInt(textView: TextView): Int{
        var data: Int = 0
        try {
            data = textView.text.toString().toInt()
        } catch(e: NullPointerException) {
            data = -1
        } catch(e: NumberFormatException) {
            data = -2
        }
        return data
    }

    private fun degToSweetLevel(deg: Float): String {
        lateinit var taste: String
        when  {
            deg >= 6.0 -> {
                taste = getString(R.string.taste_strongly_spicy)
            }
            deg >= 3.5 -> {
                taste = getString(R.string.taste_spicy)
            }
            deg >= 1.5 -> {
                taste = getString(R.string.taste_softly_spicy)
            }
            deg >= -1.4 -> {
                taste = getString(R.string.taste_normal)
            }
            deg >= -3.4 -> {
                taste = getString(R.string.taste_softly_sweet)
            }
            deg >= -5.9 -> {
                taste = getString(R.string.taste_sweet)
            }
            deg <= -6.0 -> {
                taste = getString(R.string.taste_strongly_sweet)
            }
            else -> {
                taste = "不正値"
            }
        }

        return taste
    }

    private fun validateSakeList() : Boolean{
        var msg: String = ""

        val initText = resources.getString(R.string.sake_information_name_init)
        val needSelect = resources.getString(R.string.need_selecting)

        // 必須項目: 酒名
        if (sake_information_name.text.toString() == initText) {
            sake_information_must1.visibility = View.VISIBLE
            msg += "酒名 "
        }
        else {
            sake_information_must1.visibility = View.GONE
        }

        // 必須項目: 酒類
        if (sake_information_grade.text.toString() == needSelect) {
            sake_information_must2.visibility = View.VISIBLE
            msg += "酒類 "
        }
        else {
            sake_information_must2.visibility = View.GONE
        }

        // 必須項目: 分類
        if (sake_information_type.text.toString() == needSelect) {
            sake_information_must3.visibility = View.VISIBLE
            msg += "分類 "
        }
        else {
            sake_information_must3.visibility = View.GONE
        }

        if (msg != "") {
            msg += "は必須項目です"
            sake_information_scroll.fullScroll(ScrollView.FOCUS_UP)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        }

        return msg == ""
    }

    // ---------------------------------------------------
    // -------------       Dialogs     -------------------
    // ---------------------------------------------------
    private fun dialogSakeGrade(context : Context) {
        val gradeList : Array<String> = arrayOf("純米大吟醸", "大吟醸", "純米吟醸",
            "吟醸", "特別純米", "特別本醸造", "純米", "本醸造", "その他")

        // 選択されていた値を取り出し
        var grade : Int = gradeList.indexOf(sake_information_grade.text)
        if (grade < 0) { grade = 0}

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.sake_grade)
            .setSingleChoiceItems(gradeList, grade) { _, which ->
                grade = which
            }
            .setPositiveButton("OK") { _, _ ->
                sake_information_grade.text = gradeList[grade]
            }
            .show()
    }

    private fun dialogSakeType(context : Context) {
        val typeList : Array<String> = arrayOf(
            "生酛", "山廃酛", //　酛
            "生詰め酒", "生貯蔵酒", "生酒", // 火入れ
            "原酒",  // 加水
            "冷やおろし", "雪室貯蔵", "樽酒", "長期熟成酒", // 貯蔵
            "新酒", "古酒", // 新古
            "にごり酒", "おり酒",  // 濾し
            "荒走り", "中汲み", "責め", "雫酒", // 絞り
            "凍結酒", "発泡酒", // 口当たり
            "生一本", "貴醸酒" // 製造
        )
        var checked : BooleanArray = BooleanArray(typeList.size) { false }

        // 選択されていた値を取り出し
        for (type in sake_information_type.text.split(",")) {
            var idx = typeList.indexOf(type)
            Log.d("asdf", idx.toString())
            if (idx > 0) {
                checked[idx] = true
            }
        }

        var types : String = ""
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.sake_type)
            .setMultiChoiceItems(typeList, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                for (i in 0 until typeList.size-1) {
                    if (checked[i]) {
                        Log.d("checked", typeList[i])
                        types += typeList[i] + ","
                    }
                }
                sake_information_type.text = types
            }
            .show()
    }

    private fun dialogSakePrefecture(context : Context) {
        val prefectureList : Array<String> = arrayOf(
            "北海道", // 北海道
            "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", // 東北
            "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県", // 関東
            "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県", // 中部
            "三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県", // 近畿
            "鳥取県", "島根県", "岡山県", "広島県", "山口県", // 中国
            "徳島県", "香川県", "愛媛県", "高知県", // 四国
            "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県" // 九州・沖縄
        )

        // 選択されていた値を取り出し
        var pref : Int = prefectureList.indexOf(sake_information_prefecture.text)
        if (pref < 0) { pref = 0}

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.sake_prefecture)
            .setSingleChoiceItems(prefectureList, pref) { _, which ->
                pref = which
            }
            .setPositiveButton("OK") { _, _ ->
                sake_information_prefecture.text = prefectureList[pref]
            }
            .show()
    }


}