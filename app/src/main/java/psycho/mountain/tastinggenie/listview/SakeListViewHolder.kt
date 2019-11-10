package psycho.mountain.tastinggenie.listview

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.sake_list_item.*  // TODO: extensionが効かない
import psycho.mountain.tastinggenie.R

class SakeListViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    val sakeListItem: CardView = view.findViewById(R.id.sake_list_item)
    val sakeImageView: ImageView = view.findViewById(R.id.sake_list_image)
    val nameTextView: TextView = view.findViewById(R.id.sake_list_name)
    val gradeTextView: TextView = view.findViewById(R.id.sake_list_grade)
    val typeTextView: TextView = view.findViewById(R.id.sake_list_type)

}