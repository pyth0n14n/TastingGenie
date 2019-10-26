package psycho.mountain.tastinggenie

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sake_list.*
import kotlinx.android.synthetic.main.fragment_sake_list.view.*
import psycho.mountain.tastinggenie.database.SakeDBManager
import psycho.mountain.tastinggenie.database.SakeList
import psycho.mountain.tastinggenie.listview.RecyclerAdapter
import psycho.mountain.tastinggenie.listview.RecyclerViewHolder

class SakeListFragment: Fragment() {

    lateinit var sakeDBManager: SakeDBManager
    lateinit var sakeList: List<SakeList>
    private var listener: SakeListListener? = null

    interface SakeListListener {
        fun onItemClick(sake: SakeList)
        fun onFabButtonClick()
    }

    companion object {
        fun newInstance() : SakeListFragment {
            return SakeListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sake_list, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SakeListFragment.SakeListListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list

        context?.let {
            sakeDBManager = SakeDBManager(it)
            sakeList = sakeDBManager.getSakeList()
            val adapter = RecyclerAdapter(sakeList, object : RecyclerViewHolder.ItemClickListener {
                    override fun onItemClick(position: Int) {
                        listener?.let{
                            it.onItemClick(sakeList[position])
                        }
                    }
                })

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(activity, 2)
            recyclerView.adapter = adapter
        }

        view.button_sake_list_add.setOnClickListener {
            listener?.let{
                it.onFabButtonClick()
            }
        }
    }
}