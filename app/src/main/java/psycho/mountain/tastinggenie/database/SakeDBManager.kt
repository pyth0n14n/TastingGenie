package psycho.mountain.tastinggenie.database

import android.content.Context
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.insert

class SakeDBManager (context: Context){

    private val sakeListDb = SakeDBOpenHelper.newInstance(context)

    fun getSakeList(): List<SakeList> {
        lateinit var sakeList: List<SakeList>
        sakeListDb.use {
            sakeList = select(SakeDBOpenHelper.TABLE_SAKE_LIST).parseList(SakeListParser())
        }
        return sakeList
    }

    fun insertSakeList(name: String, grade: String, type: String, image: String, maker: String,
                       pref: String, pol_rate: Int, alcohol: Int, rice: String, yeast: String) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_NAME to name,
                SakeDBOpenHelper.COL_GRADE to grade,
                SakeDBOpenHelper.COL_TYPE to type,
                SakeDBOpenHelper.COL_IMAGE to image,
                SakeDBOpenHelper.COL_MAKER to maker,
                SakeDBOpenHelper.COL_PREF to pref,
                SakeDBOpenHelper.COL_POL_RATE to pol_rate,
                SakeDBOpenHelper.COL_ALCOHOL to alcohol,
                SakeDBOpenHelper.COL_RICE to rice,
                SakeDBOpenHelper.COL_YEAST to yeast
                )
        }
    }

    fun deleteSakeList(id: Int) {
        sakeListDb.use {
            delete(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_ID + " = ?", arrayOf(id.toString())
            )
        }
    }

    fun insertSakeReview(id: Int, date: String, temp: String, color: String,
                         scent1: String, scent2: String, scent3: String,
                         sweet: String, sour: String, bitter: String, umami: String,
                         viscosity: String, scene: String, dish: String, comment: String,
                         review: Float) {

        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_REVIEW,
                SakeDBOpenHelper.COL_ID to id,
                SakeDBOpenHelper.COL_DATE to date,
                SakeDBOpenHelper.COL_TEMP to temp,
                SakeDBOpenHelper.COL_COLOR to color,
                SakeDBOpenHelper.COL_SCENT1 to scent1,
                SakeDBOpenHelper.COL_SCENT2 to scent2,
                SakeDBOpenHelper.COL_SCENT3 to scent3,
                SakeDBOpenHelper.COL_SWEET to sweet,
                SakeDBOpenHelper.COL_SOUR to sour,
                SakeDBOpenHelper.COL_BITTER to bitter,
                SakeDBOpenHelper.COL_UMAMI to umami,
                SakeDBOpenHelper.COL_VISCOSITY to viscosity,
                SakeDBOpenHelper.COL_SCENE to scene,
                SakeDBOpenHelper.COL_DISH to dish,
                SakeDBOpenHelper.COL_COMMENT to comment,
                SakeDBOpenHelper.COL_REVIEW to review
            )
        }
    }

    fun deleteSakeReview(review_id: Int) {
        sakeListDb.use {
            delete(
                SakeDBOpenHelper.TABLE_SAKE_REVIEW,
                SakeDBOpenHelper.COL_REVIEW_ID + " = ?", arrayOf(review_id.toString())
            )
        }
    }

}