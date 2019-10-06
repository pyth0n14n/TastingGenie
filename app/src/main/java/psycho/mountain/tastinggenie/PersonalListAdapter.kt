package psycho.mountain.tastinggenie

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.row.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.layoutInflater
import java.io.ByteArrayInputStream

class PersonalListAdapter : ArrayAdapter<ListData> {

    // TODO: これは何をやっているのか？
    constructor(context: Context?, resource: Int) : super(context, resource)
    constructor(context: Context?, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId)
    constructor(context: Context?, resource: Int, objects: Array<out ListData>?) : super(context, resource, objects)
    constructor(context: Context?, resource: Int, textViewResourceId: Int, objects: Array<out ListData>?) : super(context, resource, textViewResourceId, objects)
    constructor(context: Context?, resource: Int, objects: MutableList<ListData>?) : super(context, resource, objects)
    constructor(context: Context?, resource: Int, textViewResourceId: Int, objects: MutableList<ListData>?) : super(context, resource, textViewResourceId, objects)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mView = convertView ?: context.layoutInflater.inflate(R.layout.row, null)

        getItem(position)?.run {
            //val imageArray : ByteArray = image.getBytes(1, image.length().toInt())
            val imageBitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

            mView.image_personal.imageBitmap = imageBitmap
            mView.rowFirstName.text = first_name
            mView.rowLastName.text = last_name
        }
        return mView
    }



}