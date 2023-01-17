package com.example.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.R

class StoryEmailEditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener {
            if (it.isNullOrEmpty()) {
                error = "Email must not be empty"
            } else if (!it.contains('@')) {
                error = "Email is not valid"
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.rounded_edit_text_state)
        hint = resources.getString(R.string.hint_email)
        textSize = 16f
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }
}