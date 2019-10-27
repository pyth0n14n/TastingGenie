package psycho.mountain.tastinggenie.database

import android.content.Context
import org.jetbrains.anko.db.IntParser
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
                       pref: String, sake_deg: Float, pol_rate: Int, alcohol: Int, rice: String,
                       yeast: String) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_NAME to name,
                SakeDBOpenHelper.COL_GRADE to grade,
                SakeDBOpenHelper.COL_TYPE to type,
                SakeDBOpenHelper.COL_IMAGE to image,
                SakeDBOpenHelper.COL_MAKER to maker,
                SakeDBOpenHelper.COL_PREF to pref,
                SakeDBOpenHelper.COL_SAKE_DEG to sake_deg,
                SakeDBOpenHelper.COL_POL_RATE to pol_rate,
                SakeDBOpenHelper.COL_ALCOHOL to alcohol,
                SakeDBOpenHelper.COL_RICE to rice,
                SakeDBOpenHelper.COL_YEAST to yeast
                )
        }
    }

    fun insertSakeListFromList(sakeList: SakeList) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                // IDは自動で振ってもらう
                SakeDBOpenHelper.COL_NAME to sakeList.name,
                SakeDBOpenHelper.COL_GRADE to sakeList.grade,
                SakeDBOpenHelper.COL_TYPE to sakeList.type,
                SakeDBOpenHelper.COL_IMAGE to sakeList.image,
                SakeDBOpenHelper.COL_MAKER to sakeList.maker,
                SakeDBOpenHelper.COL_PREF to sakeList.prefecture,
                SakeDBOpenHelper.COL_SAKE_DEG to sakeList.sake_deg,
                SakeDBOpenHelper.COL_POL_RATE to sakeList.pol_rate,
                SakeDBOpenHelper.COL_ALCOHOL to sakeList.alcohol,
                SakeDBOpenHelper.COL_RICE to sakeList.rice,
                SakeDBOpenHelper.COL_YEAST to sakeList.yeast
            )
        }
    }

    fun insertSakeListFromListWithId(sakeList: SakeList) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_ID to sakeList.id,
                SakeDBOpenHelper.COL_NAME to sakeList.name,
                SakeDBOpenHelper.COL_GRADE to sakeList.grade,
                SakeDBOpenHelper.COL_TYPE to sakeList.type,
                SakeDBOpenHelper.COL_IMAGE to sakeList.image,
                SakeDBOpenHelper.COL_MAKER to sakeList.maker,
                SakeDBOpenHelper.COL_PREF to sakeList.prefecture,
                SakeDBOpenHelper.COL_SAKE_DEG to sakeList.sake_deg,
                SakeDBOpenHelper.COL_POL_RATE to sakeList.pol_rate,
                SakeDBOpenHelper.COL_ALCOHOL to sakeList.alcohol,
                SakeDBOpenHelper.COL_RICE to sakeList.rice,
                SakeDBOpenHelper.COL_YEAST to sakeList.yeast
            )
        }
    }

    fun isExistSakeList(id: Int): Boolean {
        var dbId: Int = -99
        // 引数のIDの要素を要求してみて，存在するかを判断する
        sakeListDb.use {
            dbId = select(SakeDBOpenHelper.TABLE_SAKE_LIST, "id")
                .whereArgs("id = {id}", "id" to id).parseSingle(IntParser)
        }
        return dbId == id

    }

    fun replaceSakeListFromList(sakeList: SakeList) {
        // あるIDのリストをリプレイスしてアップデートする
        deleteSakeList(sakeList.id)
        insertSakeListFromListWithId(sakeList)
    }

    fun updateSakeListFromList(sakeList: SakeList) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_NAME to sakeList.name,
                SakeDBOpenHelper.COL_GRADE to sakeList.grade,
                SakeDBOpenHelper.COL_TYPE to sakeList.type,
                SakeDBOpenHelper.COL_IMAGE to sakeList.image,
                SakeDBOpenHelper.COL_MAKER to sakeList.maker,
                SakeDBOpenHelper.COL_PREF to sakeList.prefecture,
                SakeDBOpenHelper.COL_SAKE_DEG to sakeList.sake_deg,
                SakeDBOpenHelper.COL_POL_RATE to sakeList.pol_rate,
                SakeDBOpenHelper.COL_ALCOHOL to sakeList.alcohol,
                SakeDBOpenHelper.COL_RICE to sakeList.rice,
                SakeDBOpenHelper.COL_YEAST to sakeList.yeast
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
                         scent_top: String, scent_mouth: String,
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
                SakeDBOpenHelper.COL_SCENT_TOP to scent_top,
                SakeDBOpenHelper.COL_SCENT_MOUTH to scent_mouth,
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