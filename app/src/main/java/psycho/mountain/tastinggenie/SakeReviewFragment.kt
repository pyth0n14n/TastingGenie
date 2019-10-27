package psycho.mountain.tastinggenie

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SakeReviewFragment: Fragment() {

    companion object {
        fun newInstance(): SakeReviewFragment {
            return SakeReviewFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sake_review, container, false)
    }



}