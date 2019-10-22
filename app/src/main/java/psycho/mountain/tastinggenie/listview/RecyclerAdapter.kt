package psycho.mountain.tastinggenie.listview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import psycho.mountain.tastinggenie.R
import psycho.mountain.tastinggenie.database.ListData

class RecyclerAdapter(
    private val itemList:List<ListData>,
    private val itemClickListener: RecyclerViewHolder.ItemClickListener) : RecyclerView.Adapter<RecyclerViewHolder>() {


    override fun getItemCount(): Int{
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.sake_list_item, parent, false)

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val myHolder:RecyclerViewHolder = holder

        val personal = itemList[position]

        myHolder.sakeImageView.setImageResource(R.mipmap.ic_launcher)  // TODO: 本物の画像
        myHolder.nameTextView.text = personal.first_name
        myHolder.gradeTextView.text = personal.last_name

        myHolder.sakeListItem.setOnClickListener{
            itemClickListener.onItemClick(position)
        }

    }
}
