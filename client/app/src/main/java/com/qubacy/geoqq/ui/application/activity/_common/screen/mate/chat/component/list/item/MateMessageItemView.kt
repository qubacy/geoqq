package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.textview.MaterialTextView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentMateMessageBinding
import com.qubacy.geoqq.ui._common.util.theme.extension.resolveColorAttr
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.message.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData

class MateMessageItemView(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), RecyclerViewItemViewProvider<MateMessageItemData> {
    companion object {
        const val TAG = "MateMessageItemView"

        val BACKGROUND_DRAWABLE_RIGHT_RES_ID = R.drawable.message_back_right_new
        val BACKGROUND_DRAWABLE_LEFT_RES_ID = R.drawable.message_back_left_new

        const val MIN_WIDTH_PERCENT = 0.8f
        const val WIDTH_BREAKPOINT_PX = 600

        val VERTICAL_PADDING_DIMEN_ID = R.dimen.tiny_gap_between_components
    }

    // todo: optimize it:
    private lateinit var mBackgroundRight: Drawable
    private lateinit var mBackgroundLeft: Drawable

    @ColorInt
    private var mBackgroundTintRight: Int = 0
    @ColorInt
    private var mBackgroundTintLeft: Int = 0

    private var mVerticalPadding: Int = 0

    private lateinit var mBinding: ComponentMateMessageBinding
    private var mTextView: MaterialTextView? = null

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

        mVerticalPadding = context.resources.getDimension(VERTICAL_PADDING_DIMEN_ID).toInt()
    }

    private fun inflate() {
        val layoutInflater = LayoutInflater.from(context)

        mBinding = ComponentMateMessageBinding.inflate(layoutInflater, this)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        initLayoutAttrs()
    }

    private fun initLayoutAttrs() {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        orientation = VERTICAL

        updatePadding(top = mVerticalPadding, bottom = mVerticalPadding)
    }

    override fun setData(data: MateMessageItemData) {
        if (data.text != null) setText(data.text)

        mBinding.componentMateMessageTimestamp.text = data.timestamp

        changeLayoutBySenderSide(data.senderSide)
    }

    // todo: is it ok? search for the better way..
    private fun setText(text: String) {
        if (mTextView == null) mTextView = initTextView()

        mTextView!!.text = text
    }

    private fun initTextView(): MaterialTextView {
        val textView = mBinding.componentMateMessageTextStub.inflate()

        mBinding.componentMateMessageTimestamp.updateLayoutParams {
            this as ConstraintLayout.LayoutParams

            this.topToTop = ConstraintLayout.LayoutParams.UNSET
            this.topToBottom = textView.id
        }

        return textView as MaterialTextView
    }

    private fun changeLayoutBySenderSide(senderSide: SenderSide) {
        changeLayoutConstraintsBySenderSide(senderSide)
        changeBackgroundBySenderSide(senderSide)
    }

    private fun changeLayoutConstraintsBySenderSide(senderSide: SenderSide) {
        mBinding.componentMateMessageContentWrapper.updateLayoutParams {
            this as LayoutParams

            this.gravity = when (senderSide) {
                SenderSide.ME -> { GravityCompat.END }
                SenderSide.OTHER -> { GravityCompat.START }
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

        mBinding.componentMateMessageContentWrapper.apply {
            background = backgroundDrawable
            backgroundTintList = ColorStateList.valueOf(backgroundTint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMinWidth()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun setMinWidth() {
        val parentWidth = (parent as ViewGroup).measuredWidth

        mBinding.componentMateMessageContentWrapper.updateLayoutParams {
            this as LayoutParams

            this.width = getMinWidthByParentWidth(parentWidth)
        }
    }

    fun getMinWidthByParentWidth(parentWidth: Int): Int {
        return if (parentWidth > WIDTH_BREAKPOINT_PX) (parentWidth * MIN_WIDTH_PERCENT).toInt()
        else parentWidth
    }

    fun getContentWrapper(): ConstraintLayout {
        return mBinding.componentMateMessageContentWrapper
    }

    override fun getView(): View {
        return this
    }
}