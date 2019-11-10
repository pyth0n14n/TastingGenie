package psycho.mountain.tastinggenie.listview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import psycho.mountain.tastinggenie.R
import psycho.mountain.tastinggenie.database.SakeReview

class SakeReviewAdapter (
    private val itemList: List<SakeReview>,
    private val itemClickListener: SakeReviewViewHolder.ItemClickListener): RecyclerView.Adapter<SakeReviewViewHolder>(){

    override fun getItemCount(): Int{
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SakeReviewViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.sake_review_item, parent, false)

        return SakeReviewViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SakeReviewViewHolder, position: Int) {
        val holder:SakeReviewViewHolder = viewHolder

        val review: SakeReview = itemList[position]

        holder.dateTextView.text = review.date
        holder.commentTextView.text = review.comment

        holder.sakeReviewItem.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
        holder.sakeReviewItem.setOnLongClickListener {
            itemClickListener.onItemLongClick(position)
            true
        }
    }


}

