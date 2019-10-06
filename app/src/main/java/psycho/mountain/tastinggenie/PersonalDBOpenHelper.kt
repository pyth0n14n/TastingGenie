package psycho.mountain.tastinggenie

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class PersonalDBOpenHelper (context: Context):ManagedSQLiteOpenHelper(context, DB_PERSONAL,null, DB_VERSION) {

    companion object {
        const val DB_PERSONAL = "personal.db"
        const val DB_VERSION = 2

        const val TABLE_PERSONAL = "personal"

        const val CULM_ID = "id"
        const val CULM_IMAGE = "image"
        const val CULM_FIRST_NAME = "first_name"
        const val CULM_LAST_NAME = "last_name"


        private var instance :PersonalDBOpenHelper? = null;

        fun newInstance(context: Context):PersonalDBOpenHelper{
            return instance ?: PersonalDBOpenHelper(context.applicationContext)!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            createTable(TABLE_PERSONAL, ifNotExists = true,
                columns = *arrayOf(
                    CULM_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                    CULM_IMAGE to BLOB,
                    CULM_FIRST_NAME to TEXT,
                    CULM_LAST_NAME to TEXT))
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // v2に画像追加のため，DBを一新する
        //if (oldVersion == 1 && newVersion == 2) {
            db?.run {
                dropTable(TABLE_PERSONAL, true)

                createTable(TABLE_PERSONAL, ifNotExists = true,
                    columns = *arrayOf(
                        CULM_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                        CULM_IMAGE to BLOB,
                        CULM_FIRST_NAME to TEXT,
                        CULM_LAST_NAME to TEXT))
            }

        //}
    }
}

