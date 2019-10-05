package psycho.mountain.tastinggenie

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_db_fragment.*
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select


class RegisterDBFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_db_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            val helper = PersonalDatabaseOpenHelper.newInstance(it)
            var dataList = helper.readableDatabase.select(PersonalDatabaseOpenHelper.tableName)
                .parseList<ListData>(ListDataParser())

            if (dataList.isEmpty()) {
                dataList = arrayListOf<ListData>(ListData("hoge", "huga"))
            }

            personal_list.adapter = PersonalListAdapter(context, R.layout.row).apply {
                addAll(dataList)
            }


            button_register_db!!.setOnClickListener {
                Log.d("first_name", edittext_first_name.text.toString())
                Log.d("last_name", edittext_last_name.text.toString())

                helper.use {
                    insert(
                        PersonalDatabaseOpenHelper.tableName, *arrayOf(
                            "first_name" to edittext_first_name.text.toString(), "last_name" to edittext_last_name.text.toString()
                        )
                    )
                }
            }
            button_view_db!!.setOnClickListener {
                var dataList = helper.readableDatabase.select(PersonalDatabaseOpenHelper.tableName).parseList<ListData>(ListDataParser())

                if (dataList.isEmpty()) {
                    dataList = arrayListOf<ListData>(ListData("hoge", "huga"))
                }

                personal_list.adapter = PersonalListAdapter(context, R.layout.row).apply {
                    addAll(dataList)
                }
            }
        }
    }

    // factory method
    companion object {
        fun newInstance() : RegisterDBFragment {
            return RegisterDBFragment()
        }
    }
}
