package psycho.mountain.tastinggenie

import org.jetbrains.anko.db.MapRowParser
import java.sql.Blob


class PersonalDataParser : MapRowParser<ListData> {
    override fun parseRow(columns: Map<String, Any?>): ListData {
        // MEMO: DBのid, image は，Integer, Blob のはずだが，Long, ByteArrayになっている
        return ListData((columns[PersonalDBOpenHelper.CULM_ID] as Long).toInt(),
            columns[PersonalDBOpenHelper.CULM_IMAGE] as ByteArray,
            columns[PersonalDBOpenHelper.CULM_FIRST_NAME] as String,
            columns[PersonalDBOpenHelper.CULM_LAST_NAME] as String)
    }

}