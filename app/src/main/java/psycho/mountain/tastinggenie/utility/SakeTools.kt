package psycho.mountain.tastinggenie.utility

import android.content.Context
import psycho.mountain.tastinggenie.R

fun degToSweetLevel(deg: Float, context: Context): String {
    lateinit var taste: String
    when  {
        deg >= 6.0 -> {
            taste = context.getString(R.string.taste_strongly_spicy)
        }
        deg >= 3.5 -> {
            taste = context.getString(R.string.taste_spicy)
        }
        deg >= 1.5 -> {
            taste = context.getString(R.string.taste_softly_spicy)
        }
        deg >= -1.4 -> {
            taste = context.getString(R.string.taste_normal)
        }
        deg >= -3.4 -> {
            taste = context.getString(R.string.taste_softly_sweet)
        }
        deg >= -5.9 -> {
            taste = context.getString(R.string.taste_sweet)
        }
        deg <= -6.0 -> {
            taste = context.getString(R.string.taste_strongly_sweet)
        }
        else -> {
            taste = "不正値"
        }
    }

    return taste
}
