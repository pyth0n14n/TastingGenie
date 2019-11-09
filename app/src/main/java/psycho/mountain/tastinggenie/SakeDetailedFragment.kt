package psycho.mountain.tastinggenie

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_sake_detailed.*
import psycho.mountain.tastinggenie.database.SakeList
import psycho.mountain.tastinggenie.utility.degToSweetLevel

class SakeDetailedFragment: Fragment() {

    private var sakeList: SakeList? = null  // データ更新時は既存の値をBundleで渡される
    private var listener: SakeDetailedFragmentListener? = null

    interface SakeDetailedFragmentListener {
        fun onClickEditInformation(sake: SakeList)
        fun onFabReviewButtonClick()
    }

    companion object {
        fun newInstance() : SakeDetailedFragment {
            return SakeDetailedFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            sakeList = it.getParcelable("sake") as SakeList
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SakeDetailedFragmentListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sake_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sakeList?.let {
            setupView(it)
        }

        context?.let {
            sakeList?.let {
                button_edit_sake_information.setOnClickListener {
                    listener?.onClickEditInformation(sakeList!!)
                }
            }

            button_sake_review_add.setOnClickListener {
                listener?.onFabReviewButtonClick()
            }

        }
    }

    private fun setupView(sake: SakeList) {
        if (sake.image != "") {
            sake_detailed_image.setImageURI(Uri.parse(sake.image))
            sake_detailed_image.contentDescription = sake.image
        }
        setStringOrGone(sake.name, sake_detailed_name, null)
        setStringOrGone(sake.grade, sake_detailed_grade, null)

        if (sake.type != "") {
            sake_detailed_type.visibility = TextView.VISIBLE
            // その他はまとめてtypeに表示する
            val regex = Regex("その他")
            if (regex.containsMatchIn(sake.type)) {
                val saketype = sake.type.replace("その他", sake.type_other)
                sake_detailed_type.text = saketype
            } else {
                sake_detailed_type.text = sake.type
            }
        } else {
            sake_detailed_type.visibility = TextView.GONE
        }

        setStringOrGone(sake.maker, sake_detailed_maker, null)

        if (sake.prefecture != "") {
            sake_detailed_type.visibility= TextView.VISIBLE
            if (sake.maker == "") {
                sake_detailed_prefecture.setText("${sake.prefecture}")
            }else {
                sake_detailed_prefecture.setText("(${sake.prefecture})")
            }
        } else {
            sake_detailed_prefecture.visibility = TextView.GONE
        }
        if (sake.sake_deg >= 0.0F) {
            sake_detailed_sake_deg_layout.visibility = LinearLayout.VISIBLE
            sake_detailed_sake_deg.text = sake.sake_deg.toString()
            sake_detailed_sake_deg_level.text = degToSweetLevel(sake.sake_deg, context!!)
        } else {
            sake_detailed_sake_deg_layout.visibility = LinearLayout.GONE
        }
        setFloatOrGone(sake.acidity, sake_detailed_acidity, sake_detailed_acidity_layout)
        setFloatOrGone(sake.amino, sake_detailed_amino, sake_detailed_amino_layout)

        setStringOrGone(sake.koji_mai, sake_detailed_koji_mai, sake_detailed_koji_mai_layout)
        setIntOrGone(sake.koji_pol, sake_detailed_koji_pol, sake_detailed_koji_pol_layout)
        setStringOrGone(sake.kake_mai, sake_detailed_kake_mai, sake_detailed_kake_mai_layout)
        setIntOrGone(sake.kake_pol, sake_detailed_kake_pol, sake_detailed_kake_pol_layout)

        setIntOrGone(sake.alcohol, sake_detailed_alcohol, sake_detailed_alcohol_layout)
        setStringOrGone(sake.yeast, sake_detailed_yeast, sake_detailed_yeast_layout)
        setStringOrGone(sake.water, sake_detailed_water, sake_detailed_water_layout)
    }

    private fun setStringOrGone(data: String, view: TextView, layout: LinearLayout?) {
        if (data != "") {
            view.text = data
            if (layout == null) {
                view.visibility = TextView.VISIBLE
            } else {
                layout.visibility = LinearLayout.VISIBLE
            }
        } else {
            if (layout == null) {
                view.visibility = TextView.GONE
            } else {
                layout.visibility = LinearLayout.GONE
            }
        }
    }

    private fun setIntOrGone(data: Int, view: TextView, layout: LinearLayout) {
        if (data >= 0) {
            view.text = data.toString()
            layout.visibility = LinearLayout.VISIBLE
        } else {
            layout.visibility = LinearLayout.GONE
        }
    }

    private fun setFloatOrGone(data: Float, view: TextView, layout: LinearLayout) {
        if (data >= 0) {
            view.text = data.toString()
            layout.visibility = LinearLayout.VISIBLE
        } else {
            layout.visibility = LinearLayout.GONE
        }
    }

}