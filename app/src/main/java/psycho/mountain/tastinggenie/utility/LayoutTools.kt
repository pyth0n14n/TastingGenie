package psycho.mountain.tastinggenie.utility

import android.util.Log
import android.widget.TextView

fun viewToString(textView: TextView): String{
    lateinit var data: String
    try {
        data = textView.text.toString()
    } catch(e: NullPointerException) {
        data = ""
    }
    Log.d("viewToString", data)
    return data
}

fun viewToFloat(textView: TextView): Float{
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

fun viewToInt(textView: TextView): Int{
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

