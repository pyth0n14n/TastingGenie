package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_sake_detailed.*
import kotlinx.android.synthetic.main.fragment_sake_review.*
import kotlinx.android.synthetic.main.list_checklist_item.*
import psycho.mountain.tastinggenie.database.SakeReview
import psycho.mountain.tastinggenie.listview.ExpandableCheckListAdapter
import psycho.mountain.tastinggenie.utility.viewToInt
import psycho.mountain.tastinggenie.utility.viewToString
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.math.round

class SakeReviewFragment: Fragment() {

    private var listener: SakeReviewFragmentListener? = null
    private var sakeReview: SakeReview? = null  // データ更新時は既存の値をBundleで渡される
    private var sakeListId: Int? = null         // データ初回設定時は既存の値をBundleで渡される

    interface SakeReviewFragmentListener {
        fun onClickReviewAddButton(sakeReview: SakeReview)
    }

    companion object {
        fun newInstance(): SakeReviewFragment {
            return SakeReviewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            // sakeReviewは更新時のみ渡され，それ以外では空っぽなので例外処理で対処する
            try {
                sakeReview = it.getParcelable("sakeReview") as SakeReview
            }
            catch (e: TypeCastException) {
                Log.d("SakeReviewFragment", "new review")
                sakeReview = null
            }
            sakeListId = it.getInt("sake_list_id")
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SakeReviewFragmentListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sake_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let{
            if (sakeReview != null) {
                setupView(sakeReview!!)
            }
            else {
                // 日付の自動セット
                initDate()
                // sake_list_idの設定 (MEMO: これはsakeReviewの更新だろうがそうでなかろうが，常にやらないとダメな奴だった)
                sake_review_hidden_id.text = sakeListId.toString()
            }

            // 日付のセット
            sake_review_date.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd(E)", Locale.US)  // Week の形式に応じる
                    val text_date = sdf.parse(sake_review_date.text.toString())

                    val calendar = Calendar.getInstance()
                    calendar.time = text_date
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val date = calendar.get(Calendar.DAY_OF_MONTH)

                    val dtp = DatePickerDialog(
                        context!!, DatePickerDialog.OnDateSetListener { view, y, m, d ->
                            calendar.set(y, m, d)
                            val day = calendar.get(Calendar.DAY_OF_WEEK)
                            val week = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
                            sake_review_date.setText("%04d/%02d/%02d(%s)".format(y, m+1, d, week[day-1]))
                        }, year, month, date
                    )
                    dtp.show()
                    true
                }
                else {
                    false
                }
            }

            // 温度
            sake_review_temperature.setOnClickListener {
                dialogSakeTemperature(context!!)
            }

            // 色
            sake_review_color.setOnClickListener {
                dialogSakeColor(context!!)
            }

            // 香り
            sake_review_scent_intensity.setOnClickListener {
                dialogSakeScentIntensity(context!!)
            }
            sake_review_scent_top.setOnClickListener {
                dialogSakeScent(context!!, sake_review_scent_top)
            }
            sake_review_scent_mouth.setOnClickListener {
                dialogSakeScent(context!!, sake_review_scent_mouth)
            }
            sake_review_scent_nose.setOnClickListener {
                dialogSakeScent(context!!, sake_review_scent_nose)
            }

            // 味
            sake_review_viscosity.setOnClickListener {
                dialogSakeViscosity(context!!)
            }
            sake_review_sweet.setOnClickListener {
                dialogSakeSweetSpicy(context!!)
            }
            sake_review_sour.setOnClickListener {
                dialogSakeSour(context!!)
            }
            sake_review_bitter.setOnClickListener {
                dialogSakeBitter(context!!)
            }
            sake_review_umami.setOnClickListener {
                dialogSakeUmami(context!!)
            }
            sake_review_sharp.setOnClickListener {
                dialogSakeSharp(context!!)
            }

            // 評価
            sake_review_review.setOnClickListener {
                dialogSakeReview(context!!)
            }

            // DBに追加する処理
            button_sake_review_make.setOnClickListener {
                val sakeReview = makeSakeReview()
                listener?.onClickReviewAddButton(sakeReview)
            }
        }
    }

    private fun setupView(review: SakeReview) {
        sake_review_hidden_review_id.text = review.review_id.toString()
        sake_review_hidden_id.text = review.id.toString()
        sake_review_date.setText(review.date)
        sake_review_bar.setText(review.bar)
        if (review.price >= 0) {sake_review_price.setText(review.price.toString())}
        if (review.volume >= 0) {sake_review_volume.setText(review.volume.toString())}
        sake_review_temperature.text = review.temp
        sake_review_color.text = review.color
        sake_review_viscosity.text = review.viscosity
        sake_review_scent_intensity.text = review.intensity
        sake_review_scent_top.text = review.scent_top
        sake_review_scent_mouth.text = review.scent_mouth
        sake_review_scent_nose.text = review.scent_nose
        sake_review_sweet.text = review.sweet
        sake_review_sour.text = review.sour
        sake_review_bitter.text = review.bitter
        sake_review_umami.text = review.umami
        sake_review_sharp.text = review.sharp
        sake_review_scene.setText(review.scene)
        sake_review_dish.setText(review.dish)
        sake_review_comment.setText(review.comment)
        sake_review_review.text = review.review

        button_sake_review_make.text = "更新"
    }

    private fun makeSakeReview(): SakeReview {
        return SakeReview(
            viewToInt(sake_review_hidden_review_id),
            viewToInt(sake_review_hidden_id),
            viewToString(sake_review_date),
            viewToString(sake_review_bar),
            viewToInt(sake_review_price),
            viewToInt(sake_review_volume),
            viewToString(sake_review_temperature),
            viewToString(sake_review_color),
            viewToString(sake_review_viscosity),
            viewToString(sake_review_scent_intensity),
            viewToString(sake_review_scent_top),
            viewToString(sake_review_scent_mouth),
            viewToString(sake_review_scent_nose),
            viewToString(sake_review_sweet),
            viewToString(sake_review_sour),
            viewToString(sake_review_bitter),
            viewToString(sake_review_umami),
            viewToString(sake_review_sharp),
            viewToString(sake_review_scene),
            viewToString(sake_review_dish),
            viewToString(sake_review_comment),
            viewToString(sake_review_review)
        )
    }

    private fun initDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val week = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")

        sake_review_date.setText("%04d/%02d/%02d(%s)".format(year, month+1, date, week[day-1]))
    }

    // ---------------------------------------------------
    // -------------       Dialogs     -------------------
    // ---------------------------------------------------
    private fun dialogSakeTemperature(context: Context) {
        val temperatureList = arrayOf("飛び切り燗(55℃以上)", "熱燗(50℃)", "上燗(45℃)",
            "ぬる燗(40℃)", "人肌燗(35℃)", "日向燗(30℃)", "常温(20℃)", "涼冷え(15℃)",
            "花冷え(10℃)","雪冷え(5℃)")

        var temp = temperatureList.indexOf(sake_review_temperature.text)
        if (temp < 0) {temp = 0}

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.temperature)
            .setSingleChoiceItems(temperatureList, temp) { _, which ->
                temp = which
            }
            .setPositiveButton("OK") { _, _ ->
                sake_review_temperature.text = temperatureList[temp]
            }
            .show()
    }

    private fun dialogSakeColor(context: Context) {
        val colorList = arrayOf("青冴え", "透明", "ほぼ透明", "やや黄色",
            "山吹色", "琥珀色", "番茶色", "橙色", "白色", "白濁", "緑色")

        var color = colorList.indexOf(sake_review_color.text)
        if (color < 0) {color = 0}

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.color)
            .setSingleChoiceItems(colorList, color) { _, which ->
                color = which
            }
            .setPositiveButton("OK") { _, _ ->
                sake_review_color.text = colorList[color]
            }
            .show()
    }

    private fun dialogSakeScent(context: Context, textview: TextView) {
        val listData = LinkedHashMap<String, List<String>>()

        listData["フローラル"] = arrayListOf("梅の花", "桜", "クチナシ", "チューリップ",
            "ライチ", "メロン", "バナナ", "洋ナシ", "桃")
        listData["フレッシュ"] = arrayListOf("さざんか", "りんどう", "水仙", "藤",
            "レモンバーム", "タイム",
            "ラズベリー", "イチゴ", "レモン", "ライム", "オレンジ", "グレープフルーツ",
            "クレソン", "三つ葉", "バジル",
            "松の葉", "青竹", "新芽")
        listData["穏やか"] = arrayListOf("ミネラル", "石", "木炭", "オイル",
            "白菜", "菜の花", "人参", "ごぼう", "大根", "春菊",
            "楓", "ヒノキ", "和紙",
            "くるみ", "銀杏", "胡麻",
            "ぶなしめじ", "エノキ", "まいたけ")
        listData["ふくよか"] = arrayListOf("稲穂", "つきたての餅", "白米", "豆腐", "蕎麦",
            "栗", "カシューナッツ", "ピーナッツ",
            "バター", "カスタードクリーム", "ヨーグルト",
            "磯の香", "ミズゴケ", "青のり", "カステラ", "昆布", "はちみつ", "干し椎茸")
        listData["マイナス"] = arrayListOf("セメダイン", "カビ", "アルコール", "米酢", "ゴム",
            "フィルタ紙", "炭")

        val view = ExpandableListView(context)
        val titleList: ArrayList<String> = ArrayList(listData.keys)
        val adapter = ExpandableCheckListAdapter(context, titleList, listData, textview.text.split(","), null)

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.label_scent)
            .setView(view)
            .setPositiveButton("OK") { _,_ ->
                textview.text = adapter.getCheckedItems().joinToString()
            }
            .create()

        view.setAdapter(adapter)
        dialog.show()
    }

    private fun dialogSakeSelect(context: Context, list: Array<String>, textView: TextView, title: Int) {
        var idx = list.indexOf(textView.text)
        if (idx < 0) {idx = list.size/2}

        var dialog = AlertDialog.Builder(context)
            .apply{
                setTitle(title)
                setSingleChoiceItems(list, idx) { _, which ->
                    idx = which
                }
                setPositiveButton("OK") { _, _ ->
                    textView.text = list[idx]
                }
                show()
            }
    }

    private fun dialogSakeViscosity(context: Context) {
        val viscosityList = arrayOf("弱い", "中程度", "強い")
        dialogSakeSelect(context, viscosityList, sake_review_viscosity, R.string.viscosity)
    }

    private fun dialogSakeScentIntensity(context: Context) {
        dialogSakeSelect(context,
            arrayOf("弱い", "やや弱い", "普通", "やや強い", "強い")
            , sake_review_scent_intensity, R.string.label_scent_intensity)
    }

    private fun dialogSakeSweetSpicy(context: Context) {
        dialogSakeSelect(context,
            arrayOf("甘口", "やや甘口", "普通", "やや辛口", "辛口")
            , sake_review_sweet, R.string.label_sweet_spicy)
    }

    private fun dialogSakeSour(context: Context) {
        dialogSakeSelect(context,
            arrayOf("弱い", "やや弱い", "普通", "やや強い", "強い")
            , sake_review_sour, R.string.label_sour)
    }

    private fun dialogSakeBitter(context: Context) {
        dialogSakeSelect(context,
            arrayOf("弱い", "やや弱い", "普通", "やや強い", "強い")
            , sake_review_bitter, R.string.label_bitter)
    }

    private fun dialogSakeUmami(context: Context) {
        dialogSakeSelect(context,
            arrayOf("淡麗", "やや淡麗", "普通", "やや濃醇", "濃醇")
            , sake_review_umami, R.string.label_umami)
    }

    private fun dialogSakeSharp(context: Context) {
        dialogSakeSelect(context,
            arrayOf("短い", "やや短い", "普通", "やや長い", "長い")
            , sake_review_sharp, R.string.label_sharp)
    }

    private fun dialogSakeReview(context: Context) {
        dialogSakeSelect(context,
            arrayOf("嫌い", "やや嫌い", "あまり好きでない",
                "普通", "やや好き", "好き", "大好き")
            , sake_review_review, R.string.label_review)
    }


}