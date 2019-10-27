package psycho.mountain.tastinggenie.listview

import android.content.res.Resources
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.imageURI
import psycho.mountain.tastinggenie.R
import psycho.mountain.tastinggenie.database.SakeList

class RecyclerAdapter(
    private val itemList:List<SakeList>,
    private val itemClickListener: RecyclerViewHolder.ItemClickListener): RecyclerView.Adapter<RecyclerViewHolder>(){


    override fun getItemCount(): Int{
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.sake_list_item, parent, false)

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerViewHolder, position: Int) {
        val holder:RecyclerViewHolder = viewHolder

        val sake: SakeList = itemList[position]

        holder.sakeImageView.imageURI = Uri.parse(sake.image)
        holder.nameTextView.text = sake.name
        holder.gradeTextView.text = sake.grade
        holder.typeTextView.text = sake.type.replace(",", " ")

        holder.sakeListItem.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
        holder.sakeListItem.setOnLongClickListener {
            itemClickListener.onItemLongClick(position)
            true
        }
    }
}
