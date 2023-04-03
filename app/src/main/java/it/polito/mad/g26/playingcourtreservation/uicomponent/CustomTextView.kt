package it.polito.mad.g26.playingcourtreservation.uicomponent

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import it.polito.mad.g26.playingcourtreservation.R

class CustomTextView(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.custom_text_view, this)

        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0)

        val title = findViewById<TextView>(R.id.title)
        val value = findViewById<TextView>(R.id.value)

        try {
            title.text = customAttributesStyle.getString(R.styleable.CustomTextView_title)
            value.text = customAttributesStyle.getString(R.styleable.CustomTextView_value)
        } finally {
            customAttributesStyle.recycle()
        }
    }
}