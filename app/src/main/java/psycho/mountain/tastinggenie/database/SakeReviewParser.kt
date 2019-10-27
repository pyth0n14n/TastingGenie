package psycho.mountain.tastinggenie.database

import org.jetbrains.anko.db.MapRowParser

class SakeReviewParser: MapRowParser<SakeReview> {
    override fun parseRow(columns: Map<String, Any?>): SakeReview {
        return SakeReview(
            (columns[SakeDBOpenHelper.COL_REVIEW_ID] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_ID] as Long).toInt(),
            columns[SakeDBOpenHelper.COL_DATE] as String,
            columns[SakeDBOpenHelper.COL_BAR] as String,
            columns[SakeDBOpenHelper.COL_PRICE] as String,
            columns[SakeDBOpenHelper.COL_TEMP] as String,
            columns[SakeDBOpenHelper.COL_COLOR] as String,
            columns[SakeDBOpenHelper.COL_VISCOSITY] as Int,
            columns[SakeDBOpenHelper.COL_SCENT_TOP] as String,
            columns[SakeDBOpenHelper.COL_SCENT_MOUTH] as String,
            (columns[SakeDBOpenHelper.COL_SWEET] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_SOUR] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_BITTER] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_UMAMI] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_SHARP] as Long).toInt(),
            columns[SakeDBOpenHelper.COL_SCENE] as String,
            columns[SakeDBOpenHelper.COL_DISH] as String,
            columns[SakeDBOpenHelper.COL_COMMENT] as String,
            (columns[SakeDBOpenHelper.COL_REVIEW] as Double).toInt()
            )
    }
}