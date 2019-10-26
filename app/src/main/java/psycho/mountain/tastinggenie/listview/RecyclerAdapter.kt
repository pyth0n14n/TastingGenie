package psycho.mountain.tastinggenie.listview

import android.content.Context
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

        val sake = itemList[position]

        myHolder.sakeImageView.imageURI = Uri.parse(sake.image)
        myHolder.nameTextView.text = sake.name
        myHolder.gradeTextView.text = sake.grade

        myHolder.sakeListItem.setOnClickListener{
            itemClickListener.onItemClick(position)
        }

    }
}
