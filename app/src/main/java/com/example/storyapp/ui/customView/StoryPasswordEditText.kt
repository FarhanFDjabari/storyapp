package com.example.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.R

class StoryPasswordEditText: AppCompatEditText, View.OnTouchListener {

    private var isObscure: Boolean = true
    private lateinit var visibilityOn: Drawable
    private lateinit var visibilityOff: Drawable

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
        visibilityOn = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
        visibilityOff = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_24) as Drawable
        layoutDirection = View.LAYOUT_DIRECTION_LTR
        addTextChangedListener {
            if (it.isNullOrEmpty()) {
                setError(resources.getString(R.string.empty_string_error), null)
            } else if (it.length < 8) {
                setError(resources.getString(R.string.less_character_password_error), null)
            }
        }
        setOnTouchListener(this)
    }

    private fun setButtonDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.rounded_edit_text_state)
        setButtonDrawable(endOfTheText = if(isObscure) visibilityOff else visibilityOn)
        transformationMethod = if (isObscure) PasswordTransformationMethod.getInstance()
            else HideReturnsTransformationMethod.getInstance()
        hint = resources.getString(R.string.hint_password)
        textSize = 16f
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            if (event?.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= right - totalPaddingEnd) {
                    isObscure = !isObscure
                    return true
                }
            }
        }
        return false
    }
}