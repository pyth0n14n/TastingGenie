package psycho.mountain.tastinggenie

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_fragment, container, false)
    }

    // singleton
    companion object {
        @JvmStatic
        fun newInstance() : TestFragment = TestFragment();
    }
}
