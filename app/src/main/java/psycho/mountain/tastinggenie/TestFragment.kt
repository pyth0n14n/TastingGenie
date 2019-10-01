package psycho.mountain.tastinggenie

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_test_fragment.*

class TestFragment : Fragment() {

    interface FragmentListener {
        fun onClickButton()
    }

    private var mListener: FragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FragmentListener){
            mListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_test_fragment!!.setOnClickListener {
            mListener?.let {
                it.onClickButton()
            }
        }
    }

    // factory method
    companion object {
        fun newInstance() : TestFragment {
            return TestFragment()
        }
    }
}
