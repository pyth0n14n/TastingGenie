package psycho.mountain.tastinggenie.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class SakeDBOpenHelper (context: Context): ManagedSQLiteOpenHelper(context,
    DB_SAKE, null, DB_VERSION) {

    companion object {
        const val DB_SAKE = "sake.db"
        const val DB_VERSION = 1

        const val TABLE_SAKE_LIST = "sake_list"
        const val TABLE_SAKE_REVIEW = "sake_review"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_GRADE = "grade"
        const val COL_TYPE = "type"
        const val COL_TYPE_OTHER = "type_other"
        const val COL_IMAGE = "image"
        const val COL_MAKER = "maker"
        const val COL_PREF = "prefecture"
        const val COL_ALCOHOL = "alcohol"
        const val COL_YEAST = "yeast"
        const val COL_WATER = "water"
        const val COL_SAKE_DEG = "sake_degree"
        const val COL_ACIDITY = "acidity"
        const val COL_AMINO = "amino"
        const val COL_KOJI_MAI = "koji_mai"
        const val COL_KOJI_POL = "koji_pol"
        const val COL_KAKE_MAI = "kake_mai"
        const val COL_KAKE_POL = "kake_pol"

        const val COL_REVIEW_ID = "review_id"
        const val COL_DATE = "date"
        const val COL_BAR = "bar"
        const val COL_PRICE = "price"
        const val COL_TEMP = "temperature"
        const val COL_COLOR = "color"
        const val COL_VISCOSITY = "viscosity"
        const val COL_SCENT_TOP = "scent_top"
        const val COL_SCENT_MOUTH = "scent_mouth"
        const val COL_SWEET = "sweet"
        const val COL_SOUR = "sour"
        const val COL_BITTER = "bitter"
        const val COL_UMAMI = "umami"
        const val COL_SHARP = "sharp"
        const val COL_SCENE = "scene"
        const val COL_DISH = "dish"
        const val COL_COMMENT = "comment"
        const val COL_REVIEW = "review"

        private var instance: SakeDBOpenHelper? = null

        fun newInstance(context: Context): SakeDBOpenHelper {
            return instance
                ?: SakeDBOpenHelper(context.applicationContext)!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            createTable(
                TABLE_SAKE_LIST, ifNotExists = true,
                columns = *arrayOf(
                    COL_ID to INTEGER + PRIMARY_KEY + UNIQUE, // INTEGER + PRIMARY_KEY + UNIQUE,
                    COL_NAME to TEXT,
                    COL_GRADE to TEXT,
                    COL_TYPE to TEXT,
                    COL_TYPE_OTHER to TEXT,
                    COL_IMAGE to TEXT,
                    COL_MAKER to TEXT,
                    COL_PREF to TEXT,
                    COL_ALCOHOL to INTEGER,
                    COL_YEAST to TEXT,
                    COL_WATER to TEXT,
                    COL_SAKE_DEG to REAL,
                    COL_ACIDITY to REAL,
                    COL_AMINO to REAL,
                    COL_KOJI_MAI to TEXT,
                    COL_KOJI_POL to INTEGER,
                    COL_KAKE_MAI to TEXT,
                    COL_KAKE_POL to INTEGER
                )
            )
            createTable(
                TABLE_SAKE_REVIEW, ifNotExists = true,
                columns = *arrayOf(
                    COL_REVIEW_ID to INTEGER + PRIMARY_KEY + UNIQUE, // INTEGER + PRIMARY_KEY + UNIQUE,
                    COL_ID to INTEGER,
                    COL_DATE to TEXT,
                    COL_BAR to TEXT,
                    COL_PRICE to TEXT,
                    COL_TEMP to TEXT,
                    COL_COLOR to TEXT,
                    COL_VISCOSITY to INTEGER,
                    COL_SCENT_TOP to TEXT,
                    COL_SCENT_MOUTH to TEXT,
                    COL_SWEET to INTEGER,
                    COL_SOUR to INTEGER,
                    COL_BITTER to INTEGER,
                    COL_UMAMI to INTEGER,
                    COL_SHARP to INTEGER,
                    COL_SCENE to TEXT,
                    COL_DISH to TEXT,
                    COL_COMMENT to TEXT,
                    COL_REVIEW to INTEGER
                )
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 今は特にすることなし
    }

}