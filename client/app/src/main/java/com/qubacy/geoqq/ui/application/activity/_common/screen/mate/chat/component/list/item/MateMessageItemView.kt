package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentMateMessageBinding
import com.qubacy.geoqq.ui._common.util.theme.extension.resolveColorAttr
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.message.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData

class MateMessageItemView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), RecyclerViewItemView<MateMessageItemData> {
    companion object {
        val BACKGROUND_DRAWABLE_RIGHT_RES_ID = R.drawable.message_background_right
        val BACKGROUND_DRAWABLE_LEFT_RES_ID = R.drawable.message_background_left

        const val MIN_WIDTH_PERCENT = 0.8f
    }

    private lateinit var mBackgroundRight: Drawable
    private lateinit var mBackgroundLeft: Drawable

    @ColorInt
    private var mBackgroundTintRight: Int = 0
    @ColorInt
    private var mBackgroundTintLeft: Int = 0

    private lateinit var mBinding: ComponentMateMessageBinding

    init {
        loadVariables(context)
        inflate()
        initAttrs(attrs)
    }

    private fun loadVariables(context: Context) {
        mBackgroundRight = AppCompatResources.getDrawable(context, BACKGROUND_DRAWABLE_RIGHT_RES_ID)!!
        mBackgroundLeft = AppCompatResources.getDrawable(context, BACKGROUND_DRAWABLE_LEFT_RES_ID)!!

        mBackgroundTintLeft = context.theme
            .resolveColorAttr(com.google.android.material.R.attr.colorSecondaryContainer)
        mBackgroundTintRight = context.theme
            .resolveColorAttr(com.google.android.material.R.attr.colorPrimaryContainer)
    }

    private fun inflate() {
        val layoutInflater = LayoutInflater.from(context)

        mBinding = ComponentMateMessageBinding.inflate(layoutInflater, this, false)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        initLayoutAttrs()
    }

    private fun initLayoutAttrs() {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    override fun setData(data: MateMessageItemData) {
        if (data.text != null) setText(data.text)

        mBinding.componentMateMessageTimestamp.text = data.timestamp

        changeLayoutBySenderSide(data.senderSide)
    }

    // todo: is it ok? search for the better way..
    private fun setText(text: String) {
        var textView = mBinding.root.getViewById(R.id.component_mate_message_text)

        if (textView is ViewStub) textView = textView.inflate()

        textView as MaterialTextView

        textView.text = text
    }

    private fun changeLayoutBySenderSide(senderSide: SenderSide) {
        changeLayoutConstraintsBySenderSide(senderSide)
        changeBackgroundBySenderSide(senderSide)
    }

    private fun changeLayoutConstraintsBySenderSide(senderSide: SenderSide) {
        val layoutParams = LayoutParams(mBinding.root.layoutParams)

        when (senderSide) {
            SenderSide.ME -> {
                layoutParams.startToStart = LayoutParams.UNSET
                layoutParams.endToEnd = LayoutParams.PARENT_ID
            }
            SenderSide.OTHER -> {
                layoutParams.endToEnd = LayoutParams.UNSET
                layoutParams.startToStart = LayoutParams.PARENT_ID
            }
        }
    }

    private fun changeBackgroundBySenderSide(senderSide: SenderSide) {
        val backgroundDrawable: Drawable
        val backgroundTint: Int

        when (senderSide) {
            SenderSide.ME -> {
                backgroundDrawable = mBackgroundRight
                backgroundTint = mBackgroundTintRight
            }
            SenderSide.OTHER -> {
                backgroundDrawable = mBackgroundLeft
                backgroundTint = mBackgroundTintLeft
            }
        }

        mBinding.root.apply {
            background = backgroundDrawable
            backgroundTintList = ColorStateList.valueOf(backgroundTint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension()
    }
}