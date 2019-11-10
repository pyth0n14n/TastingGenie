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

    /*
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
    */

    fun insertSakeListFromList(sakeList: SakeList) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                // IDは自動で振ってもらう
                SakeDBOpenHelper.COL_NAME to sakeList.name,
                SakeDBOpenHelper.COL_GRADE to sakeList.grade,
                SakeDBOpenHelper.COL_TYPE to sakeList.type,
                SakeDBOpenHelper.COL_TYPE_OTHER to sakeList.type_other,
                SakeDBOpenHelper.COL_IMAGE to sakeList.image,
                SakeDBOpenHelper.COL_MAKER to sakeList.maker,
                SakeDBOpenHelper.COL_PREF to sakeList.prefecture,
                SakeDBOpenHelper.COL_ALCOHOL to sakeList.alcohol,
                SakeDBOpenHelper.COL_YEAST to sakeList.yeast,
                SakeDBOpenHelper.COL_WATER to sakeList.water,
                SakeDBOpenHelper.COL_SAKE_DEG to sakeList.sake_deg,
                SakeDBOpenHelper.COL_ACIDITY to sakeList.acidity,
                SakeDBOpenHelper.COL_AMINO to sakeList.amino,
                SakeDBOpenHelper.COL_KOJI_MAI to sakeList.koji_mai,
                SakeDBOpenHelper.COL_KOJI_POL to sakeList.koji_pol,
                SakeDBOpenHelper.COL_KAKE_MAI to sakeList.kake_mai,
                SakeDBOpenHelper.COL_KAKE_POL to sakeList.kake_pol
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
                SakeDBOpenHelper.COL_TYPE_OTHER to sakeList.type_other,
                SakeDBOpenHelper.COL_IMAGE to sakeList.image,
                SakeDBOpenHelper.COL_MAKER to sakeList.maker,
                SakeDBOpenHelper.COL_PREF to sakeList.prefecture,
                SakeDBOpenHelper.COL_ALCOHOL to sakeList.alcohol,
                SakeDBOpenHelper.COL_YEAST to sakeList.yeast,
                SakeDBOpenHelper.COL_WATER to sakeList.water,
                SakeDBOpenHelper.COL_SAKE_DEG to sakeList.sake_deg,
                SakeDBOpenHelper.COL_ACIDITY to sakeList.acidity,
                SakeDBOpenHelper.COL_AMINO to sakeList.amino,
                SakeDBOpenHelper.COL_KOJI_MAI to sakeList.koji_mai,
                SakeDBOpenHelper.COL_KOJI_POL to sakeList.koji_pol,
                SakeDBOpenHelper.COL_KAKE_MAI to sakeList.kake_mai,
                SakeDBOpenHelper.COL_KAKE_POL to sakeList.kake_pol
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


    fun getSakeListById(id: Int): SakeList {
        lateinit var sakeList: SakeList
        sakeListDb.use {
            sakeList = select(SakeDBOpenHelper.TABLE_SAKE_LIST)
                .whereArgs("id = {id}", "id" to id).parseList(SakeListParser())[0]
        }
        return sakeList
    }
    /*
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
     */

    fun deleteSakeList(id: Int) {
        sakeListDb.use {
            delete(
                SakeDBOpenHelper.TABLE_SAKE_LIST,
                SakeDBOpenHelper.COL_ID + " = ?", arrayOf(id.toString())
            )
        }
    }

    fun insertSakeReviewFromList(review: SakeReview) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_REVIEW,
                SakeDBOpenHelper.COL_ID to review.id,
                SakeDBOpenHelper.COL_DATE to review.date,
                SakeDBOpenHelper.COL_BAR to review.bar,
                SakeDBOpenHelper.COL_PRICE to review.price,
                SakeDBOpenHelper.COL_VOLUME to review.volume,
                SakeDBOpenHelper.COL_TEMP to review.temp,
                SakeDBOpenHelper.COL_COLOR to review.color,
                SakeDBOpenHelper.COL_VISCOSITY to review.viscosity,
                SakeDBOpenHelper.COL_SCENT_INTENSITY to review.intensity,
                SakeDBOpenHelper.COL_SCENT_TOP to review.scent_top,
                SakeDBOpenHelper.COL_SCENT_MOUTH to review.scent_mouth,
                SakeDBOpenHelper.COL_SCENT_NOSE to review.scent_nose,
                SakeDBOpenHelper.COL_SWEET to review.sweet,
                SakeDBOpenHelper.COL_SOUR to review.sour,
                SakeDBOpenHelper.COL_BITTER to review.bitter,
                SakeDBOpenHelper.COL_UMAMI to review.umami,
                SakeDBOpenHelper.COL_SHARP to review.sharp,
                SakeDBOpenHelper.COL_SCENE to review.scene,
                SakeDBOpenHelper.COL_DISH to review.dish,
                SakeDBOpenHelper.COL_COMMENT to review.comment,
                SakeDBOpenHelper.COL_REVIEW to review.review
            )
        }
    }

    fun insertSakeReviewFromListWithId(review: SakeReview) {
        sakeListDb.use {
            insert(
                SakeDBOpenHelper.TABLE_SAKE_REVIEW,
                SakeDBOpenHelper.COL_REVIEW_ID to review.review_id,
                SakeDBOpenHelper.COL_ID to review.id,
                SakeDBOpenHelper.COL_DATE to review.date,
                SakeDBOpenHelper.COL_BAR to review.bar,
                SakeDBOpenHelper.COL_PRICE to review.price,
                SakeDBOpenHelper.COL_VOLUME to review.volume,
                SakeDBOpenHelper.COL_TEMP to review.temp,
                SakeDBOpenHelper.COL_COLOR to review.color,
                SakeDBOpenHelper.COL_VISCOSITY to review.viscosity,
                SakeDBOpenHelper.COL_SCENT_INTENSITY to review.intensity,
                SakeDBOpenHelper.COL_SCENT_TOP to review.scent_top,
                SakeDBOpenHelper.COL_SCENT_MOUTH to review.scent_mouth,
                SakeDBOpenHelper.COL_SCENT_NOSE to review.scent_nose,
                SakeDBOpenHelper.COL_SWEET to review.sweet,
                SakeDBOpenHelper.COL_SOUR to review.sour,
                SakeDBOpenHelper.COL_BITTER to review.bitter,
                SakeDBOpenHelper.COL_UMAMI to review.umami,
                SakeDBOpenHelper.COL_SHARP to review.sharp,
                SakeDBOpenHelper.COL_SCENE to review.scene,
                SakeDBOpenHelper.COL_DISH to review.dish,
                SakeDBOpenHelper.COL_COMMENT to review.comment,
                SakeDBOpenHelper.COL_REVIEW to review.review
            )
        }
    }


    fun getSakeReviewBySakeListId(sake_list_id: Int): List<SakeReview> {
        lateinit var sakeReview: List<SakeReview>
        sakeListDb.use {
            sakeReview = select(SakeDBOpenHelper.TABLE_SAKE_REVIEW)
                .whereArgs("id = {id}", "id" to sake_list_id).parseList(SakeReviewParser())
        }
        return sakeReview
    }

    fun replaceSakeReviewFromList(review: SakeReview) {
        // あるIDのリストをリプレイスしてアップデートする
        deleteSakeReview(review.review_id)
        insertSakeReviewFromListWithId(review)
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