package psycho.mountain.tastinggenie

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_test_fragment.*

class TestFragment : Fragment() {

    interface TestFragmentListener {
        fun onClickTestButton1()
        fun onClickTestButton2()
    }

    private var mListener: TestFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is TestFragmentListener){
            mListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_test_fragment!!.setOnClickListener {
            mListener?.let {
                it.onClickTestButton1()
            }
        }
        button_to_sake_list!!.setOnClickListener {
            mListener?.let {
                it.onClickTestButton2()
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
