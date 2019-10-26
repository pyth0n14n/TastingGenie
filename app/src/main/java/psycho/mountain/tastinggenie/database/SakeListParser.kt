package psycho.mountain.tastinggenie.database

import org.jetbrains.anko.db.MapRowParser

class SakeListParser: MapRowParser<SakeList> {
    override fun parseRow(columns: Map<String, Any?>): SakeList {
        return SakeList(
            (columns[SakeDBOpenHelper.COL_ID] as Long).toInt(),
            columns[SakeDBOpenHelper.COL_NAME] as String,
            columns[SakeDBOpenHelper.COL_GRADE] as String,
            columns[SakeDBOpenHelper.COL_TYPE] as String,
            columns[SakeDBOpenHelper.COL_IMAGE] as String,
            columns[SakeDBOpenHelper.COL_MAKER] as String,
            columns[SakeDBOpenHelper.COL_PREF] as String,
            (columns[SakeDBOpenHelper.COL_SAKE_DEG] as Double).toFloat(),
            (columns[SakeDBOpenHelper.COL_POL_RATE] as Long).toInt(),
            (columns[SakeDBOpenHelper.COL_ALCOHOL] as Long).toInt(),
            columns[SakeDBOpenHelper.COL_RICE] as String,
            columns[SakeDBOpenHelper.COL_YEAST] as String
        )
    }
}