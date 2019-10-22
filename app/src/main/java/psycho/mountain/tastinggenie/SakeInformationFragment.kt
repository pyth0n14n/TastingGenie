package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sake_information.*

class SakeInformationFragment : Fragment() {

    companion object {
        fun newInstance() : SakeInformationFragment{
            return SakeInformationFragment()
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
        }
    }

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