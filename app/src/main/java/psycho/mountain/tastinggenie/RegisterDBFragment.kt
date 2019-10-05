package psycho.mountain.tastinggenie

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_db_fragment.*


class RegisterDBFragment : Fragment() {

    interface RegisterDBFragmentListener {
        fun onClickRegisterButton()
        fun onClickViewButton()
    }

    private var mListener: RegisterDBFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_db_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is RegisterDBFragmentListener){
            mListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_register_db!!.setOnClickListener {
            mListener?.let {
                it.onClickRegisterButton()
            }
        }
        button_view_db!!.setOnClickListener {
            mListener?.let {
                it.onClickViewButton()
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
