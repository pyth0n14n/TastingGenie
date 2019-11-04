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
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_sake_review.*
import java.text.SimpleDateFormat
import java.util.*

class SakeReviewFragment: Fragment() {

    companion object {
        fun newInstance(): SakeReviewFragment {
            return SakeReviewFragment()
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
            // 日付の自動セット
            initDate()

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
            sake_review_scent_top.setOnClickListener {
                dialogSakeScent(context!!, sake_review_scent_top)
            }
            sake_review_scent_mouth.setOnClickListener {
                dialogSakeScent(context!!, sake_review_scent_mouth)
            }
        }
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
        val colorList = arrayOf("青冴え", "透明", "山吹色", "琥珀色", "番茶色", "橙色", "白色", "白濁", "緑色")

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
        val scentFloralList = arrayOf("梅の花", "桜", "クチナシ", "チューリップ",
            "ライチ", "メロン", "バナナ", "洋ナシ", "桃")
        val scentFleshList = arrayOf("さざんか", "りんどう", "水仙", "藤",
            "レモンバーム", "タイム",
            "ラズベリー", "イチゴ", "レモン", "ライム", "オレンジ", "グレープフルーツ",
            "クレソン", "三つ葉", "バジル",
            "松の葉", "青竹", "新芽")
        val scentGentleList = arrayOf("ミネラル", "石", "木炭", "オイル",
            "白菜", "菜の花", "人参", "ごぼう", "大根", "春菊",
            "楓", "ヒノキ", "和紙",
            "くるみ", "銀杏", "胡麻",
            "ぶなしめじ", "エノキ", "まいたけ")
        val scentPlumpList = arrayOf("稲穂", "つきたての餅", "白米", "豆腐", "蕎麦",
            "栗", "カシューナッツ", "ピーナッツ",
            "バター", "カスタードクリーム", "ヨーグルト",
            "磯の香", "ミズゴケ", "青のり", "カステラ", "昆布", "はちみつ", "干し椎茸")

        val scentList = scentFloralList + scentFleshList + scentGentleList + scentPlumpList

        var scent = scentList.indexOf(textview.text)
        if (scent < 0) {scent = 0}

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.label_scent)
            .setSingleChoiceItems(scentList, scent) { _, which ->
                scent = which
            }
            .setPositiveButton("OK") { _, _ ->
                textview.text = scentList[scent]
            }
            .show()
    }
}