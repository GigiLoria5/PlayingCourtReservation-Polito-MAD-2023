package it.polito.mad.g26.playingcourtreservation.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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

            val titleTextColor = customAttributesStyle.getColor(
                R.styleable.CustomTextView_titleTextColor,
                ContextCompat.getColor(context, R.color.grey)
            )
            title.setTextColor(titleTextColor)

            val valueTextColor = customAttributesStyle.getColor(
                R.styleable.CustomTextView_valueTextColor,
                ContextCompat.getColor(context, R.color.custom_black)
            )
            value.setTextColor(valueTextColor)

            val titleBackgroundColor = customAttributesStyle.getColor(
                R.styleable.CustomTextView_titleBackgroundColor,
                Color.TRANSPARENT
            )
            title.setBackgroundColor(titleBackgroundColor)

            val valueBackgroundColor = customAttributesStyle.getColor(
                R.styleable.CustomTextView_valueBackgroundColor,
                Color.TRANSPARENT
            )
            value.setBackgroundColor(valueBackgroundColor)
        } finally {
            customAttributesStyle.recycle()
        }
    }
}