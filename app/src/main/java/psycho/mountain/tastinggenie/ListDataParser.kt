package psycho.mountain.tastinggenie

import org.jetbrains.anko.db.MapRowParser

class ListDataParser : MapRowParser<ListData> {
    override fun parseRow(columns: Map<String, Any?>): ListData {
        return ListData(columns["first_name"] as String, columns["last_name"] as String)
    }
}