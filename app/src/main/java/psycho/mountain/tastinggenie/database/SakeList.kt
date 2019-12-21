package psycho.mountain.tastinggenie.database

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class SakeList(var id: Int, var name: String, var grade: String, var type: String, var type_other: String,
               var image: String, var maker: String, var prefecture: String, var city: String,
               var alcohol: Int, var yeast: String, var water: String,
               var sake_deg: Float, var acidity: Float, var amino: Float,
               var koji_mai: String, var koji_pol: Int, var kake_mai: String, var kake_pol: Int): Parcelable