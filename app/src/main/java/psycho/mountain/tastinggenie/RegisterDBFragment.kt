package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_db_fragment.*
import kotlinx.android.synthetic.main.row.view.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.imageBitmap


class RegisterDBFragment : Fragment() {

    lateinit var mPersonalManager : PersonalManager
    lateinit var mPersonalList : List<ListData>
    private var mListener : ImageSelectListener? = null

    interface ImageSelectListener {
        fun onImageSelectAction()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_db_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ImageSelectListener) {
            mListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            val dialog = AlertDialog.Builder(it)
            mPersonalManager = PersonalManager(it)
            loadList()

            button_register_db!!.setOnClickListener {
                mPersonalManager.insertPersonalList(register_db_image.drawable,
                    edittext_first_name.text.toString(),
                    edittext_last_name.text.toString())
                loadList()
            }
            button_view_db!!.setOnClickListener {
                loadList()
            }

            register_db_image!!.setOnLongClickListener {
                Log.d("onLongClick", "okokok")
                mListener?.let {
                    it.onImageSelectAction()
                }
                true
            }

            personal_list.setOnItemLongClickListener { _, view, position, id ->
                /*
                Log.d("position", position.toString())
                Log.d("id", id.toString())
                Log.d("getItem", item.toString())
                Log.d("list_first", view.rowFirstName.text.toString())
                 */

                dialog.apply {
                    setTitle("!!!確認!!!")
                    setMessage("本当に消しますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        mPersonalManager.deletePersonalList(mPersonalList[position].id)
                        loadList()
                    })
                    show()
                }
                true
            }
        }
    }

    fun loadList() {
        mPersonalList = mPersonalManager.getPersonalList()
        personal_list.adapter = PersonalListAdapter(context, R.layout.row).apply {
            addAll(mPersonalList)
        }
    }

    // factory method
    companion object {
        fun newInstance() : RegisterDBFragment {
            return RegisterDBFragment()
        }
    }
}
