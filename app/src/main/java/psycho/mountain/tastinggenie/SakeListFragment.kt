package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sake_list.*
import kotlinx.android.synthetic.main.fragment_sake_list.view.*
import psycho.mountain.tastinggenie.database.SakeList
import psycho.mountain.tastinggenie.listview.SakeListAdapter
import psycho.mountain.tastinggenie.listview.SakeListViewHolder

class SakeListFragment: Fragment() {

    lateinit var sakeList: MutableList<SakeList>
    private var listener: SakeListListener? = null

    interface SakeListListener {
        fun onItemClick(sake: SakeList)
        fun onItemLongClick(sake: SakeList)
        fun onFabButtonClick()
        fun onFabButtonLongClick()
    }

    companion object {
        fun newInstance() : SakeListFragment {
            return SakeListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            Log.d("SakeListFragment", "onCreate with Argument")
            sakeList = it.getParcelableArrayList<SakeList>("sake_list") as MutableList<SakeList>
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

        if (context is SakeListListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list
        Log.d("SakeListFragment", "onViewCreated")
        Log.d("SakeListFragment", "sake.size = " + sakeList.size.toString())

        context?.let {
            val adapter = SakeListAdapter(sakeList, object : SakeListViewHolder.ItemClickListener {
                override fun onItemClick(position: Int) {
                    listener?.onItemClick(sakeList[position])
                }

                override fun onItemLongClick(position: Int) {
                    AlertDialog.Builder(it).apply {
                        setTitle("データベースの削除")
                        setMessage("本当に消しますか？")
                        setPositiveButton("はい", DialogInterface.OnClickListener { _, _ ->

                            Log.d("SakeListFragment", "Before Remove")
                            for (i in 0 until sakeList.size) {
                                Log.d("SakeListFragment", sakeList[i].id.toString() + ": " + sakeList[i].name)
                            }

                            // DBから削除
                            listener?.onItemLongClick(sakeList[position])
                            Log.d("SakeListFragment", "After DB Remove")
                            for (i in 0 until sakeList.size) {
                                Log.d("SakeListFragment", sakeList[i].id.toString() + ": " + sakeList[i].name)
                            }

                            // 表示上の変更
                            sakeList.removeAt(position)
                            recyclerView.removeViewAt(position)
                            recyclerView.adapter?.notifyItemRemoved(position)
                            recyclerView.adapter?.notifyItemRangeChanged(position, sakeList.size)
                            recyclerView.adapter?.notifyDataSetChanged()

                            Log.d("SakeListFragment", "After recyclerView Remove")
                            for (i in 0 until sakeList.size) {
                                Log.d("SakeListFragment", sakeList[i].id.toString() + ": " + sakeList[i].name)
                            }
                        })
                    }.show()
                }
            })

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(activity, 2)
            recyclerView.adapter = adapter
        }

        view.button_sake_list_add.setOnClickListener {
            listener?.onFabButtonClick()
        }

        view.button_sake_list_add.setOnLongClickListener {
            listener?.onFabButtonLongClick()
            true
        }

    }


}