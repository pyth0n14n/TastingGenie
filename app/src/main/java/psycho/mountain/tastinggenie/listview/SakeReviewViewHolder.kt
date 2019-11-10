package psycho.mountain.tastinggenie.listview

import android.service.autofill.TextValueSanitizer
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.sake_review_item.view.*
import psycho.mountain.tastinggenie.R

class SakeReviewViewHolder (view: View): RecyclerView.ViewHolder(view) {

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    val sakeReviewItem: CardView = view.findViewById(R.id.sake_review_item)
    val dateTextView: TextView = view.findViewById(R.id.sake_review_item_date)
    val commentTextView: TextView = view.findViewById(R.id.sake_review_item_comment)

}