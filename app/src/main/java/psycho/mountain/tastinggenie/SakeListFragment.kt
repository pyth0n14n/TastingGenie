package psycho.mountain.tastinggenie

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sake_list.*
import psycho.mountain.tastinggenie.database.ListData
import psycho.mountain.tastinggenie.database.PersonalManager
import psycho.mountain.tastinggenie.listview.RecyclerAdapter
import psycho.mountain.tastinggenie.listview.RecyclerViewHolder
import java.util.Collections.addAll

class SakeListFragment: Fragment() {

    lateinit var personalManager: PersonalManager
    lateinit var personalList: List<ListData>
    private var listener: SelectListListener? = null

    interface SelectListListener {
        fun onItemClick(personal: ListData)
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

        if (context is SakeListFragment.SelectListListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list

        context?.let {
            personalManager = PersonalManager(it)
            personalList = personalManager.getPersonalList()
            Log.d("hoge", personalList[0].first_name)
            val adapter = RecyclerAdapter(personalList, object : RecyclerViewHolder.ItemClickListener {
                    override fun onItemClick(position: Int) {
                        listener?.let{
                            it.onItemClick(personalList[position])
                        }
                    }
                })

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter
        }


    }
}