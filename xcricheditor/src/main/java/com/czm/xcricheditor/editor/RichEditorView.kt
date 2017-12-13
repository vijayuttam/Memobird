package com.czm.xcricheditor.editor

import android.content.Context
import android.text.Html
import android.util.SparseArray
import android.widget.EditText
import android.widget.LinearLayout
import com.czm.xcricheditor.R

/**
 * Created by deejan on 12/9/17.
 */
class RichEditorView (context: Context, content:String?) : LinearLayout(context) {

    val editText: EditText? = EditText(context)

    init {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        editText?.setLayoutParams(params)
        editText?.setTextColor(resources.getColor(R.color.textColor))
        editText?.setBackgroundColor(resources.getColor(R.color.background))
        editText?.setHintTextColor(resources.getColor(R.color.textColorHint))
        editText?.setText(Html.fromHtml(content))
        addView(editText)

    }
}