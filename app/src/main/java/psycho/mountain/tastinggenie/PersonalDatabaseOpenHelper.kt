package psycho.mountain.tastinggenie

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable

class PersonalDatabaseOpenHelper (context: Context):ManagedSQLiteOpenHelper(context, "personal.db", null, 1) {

    companion object {
        val tableName = "personal"
        private var instance :PersonalDatabaseOpenHelper? = null;

        fun newInstance(context: Context):PersonalDatabaseOpenHelper{
            return instance ?: PersonalDatabaseOpenHelper(context.applicationContext)!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            createTable(tableName, ifNotExists = true,
                columns = *arrayOf( "first_name" to TEXT, "last_name" to TEXT))
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // TODO: 更新が必要な時？
    }
}

