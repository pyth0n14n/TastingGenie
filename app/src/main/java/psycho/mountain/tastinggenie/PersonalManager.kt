package psycho.mountain.tastinggenie

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.delete
import java.io.ByteArrayOutputStream

class PersonalManager (context: Context) {

    private val mDB = PersonalDBOpenHelper.newInstance(context)

    fun getPersonalList(): List<ListData> {
        lateinit var personal_list: List<ListData>
        mDB.use {
            personal_list = select(PersonalDBOpenHelper.TABLE_PERSONAL).parseList(PersonalDataParser())
        }
        return personal_list
    }

    fun insertPersonalList(image: Drawable, first_name: String, last_name: String) {
        val bos = ByteArrayOutputStream()
        val bitmap = (image as BitmapDrawable).bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val byteImage = bos.toByteArray()
        bos.close()

        mDB.use {
            insert(
                PersonalDBOpenHelper.TABLE_PERSONAL,
                PersonalDBOpenHelper.CULM_IMAGE to byteImage,
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