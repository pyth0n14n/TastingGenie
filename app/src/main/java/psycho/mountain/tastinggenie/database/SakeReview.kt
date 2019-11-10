package psycho.mountain.tastinggenie.database

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class SakeReview(var review_id: Int, var id: Int, var date: String, var bar: String,
                 var price: Int, var volume: Int, var temp: String,
                 var color: String, var viscosity: String,
                 var intensity: String,
                 var scent_top: String, var scent_mouth: String, var scent_nose: String,
                 var sweet: String, var sour: String, var bitter: String, var umami: String,
                 var sharp: String,
                 var scene: String, var dish: String, var comment: String, var review: String):
    Parcelable