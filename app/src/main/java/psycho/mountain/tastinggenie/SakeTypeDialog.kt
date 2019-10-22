package psycho.mountain.tastinggenie

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

// TODO: dialog_sake_typeのハードコードはイマイチ．それぞれのチェックボックスが押されているかどうかのチェックも面倒．
class SakeTypeDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val sakeTypeView = inflater.inflate(R.layout.dialog_sake_type, null)

        builder.setView(sakeTypeView)
            .setTitle(R.string.sake_type)
            .setPositiveButton("OK") { _, _ ->


            }

        return builder.create()
    }

    fun setInitialType(types : Array<String>) {

    }

}