package psycho.mountain.tastinggenie

import android.content.Context
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.delete

class PersonalManager (context: Context) {

    private val mDB = PersonalDBOpenHelper.newInstance(context)

    fun getPersonalList(): List<ListData> {
        lateinit var personal_list: List<ListData>
        mDB.use {
            personal_list = select(PersonalDBOpenHelper.TABLE_PERSONAL).parseList(classParser())
        }
        return personal_list
    }

    fun insertPersonalList(first_name: String, last_name: String) {
        mDB.use {
            insert(
                PersonalDBOpenHelper.TABLE_PERSONAL,
                PersonalDBOpenHelper.CULM_FIRST_NAME to first_name,
                PersonalDBOpenHelper.CULM_LAST_NAME to last_name
            )
        }
    }

    fun deletePersonalList(id: Int) {
        mDB.use {
            delete(
                PersonalDBOpenHelper.TABLE_PERSONAL,
                PersonalDBOpenHelper.CULM_ID + " = ?", arrayOf(id.toString())
            )
        }
    }

}