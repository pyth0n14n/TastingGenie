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
            columns[SakeDBOpenHelper.COL_SCENT1] as String,
            columns[SakeDBOpenHelper.COL_SCENT2] as String,
            columns[SakeDBOpenHelper.COL_SCENT3] as String,
            columns[SakeDBOpenHelper.COL_SWEET] as String,
            columns[SakeDBOpenHelper.COL_SOUR] as String,
            columns[SakeDBOpenHelper.COL_BITTER] as String,
            columns[SakeDBOpenHelper.COL_UMAMI] as String,
            columns[SakeDBOpenHelper.COL_VISCOSITY] as String,
            columns[SakeDBOpenHelper.COL_SCENE] as String,
            columns[SakeDBOpenHelper.COL_DISH] as String,
            columns[SakeDBOpenHelper.COL_COMMENT] as String,
            (columns[SakeDBOpenHelper.COL_REVIEW] as Long).toFloat()
            )
    }
}