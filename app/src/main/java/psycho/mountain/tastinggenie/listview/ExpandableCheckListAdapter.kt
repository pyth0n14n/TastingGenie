package psycho.mountain.tastinggenie.listview

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.sdk27.coroutines.onTouch
import psycho.mountain.tastinggenie.R


class ExpandableCheckListAdapter internal constructor(private val context: Context, private val titleList: List<String>, private val dataList: HashMap<String, List<String>>, private val chekedList: List<String>)  : BaseExpandableListAdapter() {

    // <parentID, childID>
    private val checkedItems :MutableSet<Pair<Long, Long>> = hashSetOf()

    init {
        Log.d("setCheckedItems", chekedList.joinToString { "," })
        run loop@ {
            for (scent in chekedList){
                val key =
                    dataList.values.forEachIndexed { pid, list ->
                        val cid = list.indexOf(scent)
                        if (cid != -1) {
                            checkedItems.add(Pair(pid.toLong(), cid.toLong()))
                            Log.d("asdf", "$pid, $cid")
                            return@loop
                        }
                    }
            }
        }
        Log.d("setCheckedItems", checkedItems.toString())
    }

    fun getCheckedItems(): List<String> {
        val items: MutableList<String> = mutableListOf()
        checkedItems.forEach {
            val (pid, cid) = it
            items.add(getChild(pid.toInt(), cid.toInt()) as String)
        }
        return items
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.dataList[this.titleList[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        // Viewの設定
        var convertView = convertView
        val childText = getChild(groupPosition, childPosition) as String
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_checklist_item, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expandableTextView)
        expandedListTextView.text = childText

        // チェックボックスの設定
        val cb: CheckBox = convertView!!.findViewById<CheckBox>(R.id.expandableCheckBox)
        val tag: Pair<Long, Long> = Pair(getGroupId(groupPosition), getChildId(groupPosition, childPosition))

        cb.tag = tag
        cb.isChecked = checkedItems.contains(tag)
        cb.setOnClickListener{
            Log.d("CheckBox", "clicked")
            if (cb.isChecked) {
                checkedItems.add(tag)
            } else {
                checkedItems.remove(tag)
            }
        }

        // テキストをクリックしたときもチェックさせる
        expandedListTextView.setOnClickListener {
            Log.d("text", "clicked")
            cb.performClick()
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.dataList[this.titleList[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.titleList[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val listTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listTitle)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}