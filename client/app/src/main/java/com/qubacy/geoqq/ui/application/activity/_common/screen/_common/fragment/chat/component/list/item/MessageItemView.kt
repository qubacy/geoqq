package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item

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
import com.qubacy.geoqq.databinding.ComponentMessageBinding
import com.qubacy.geoqq.ui._common.util.theme.extension.resolveColorAttr
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.item.BaseRecyclerViewItemViewProvider

open class MessageItemView<MessageItemDataType : MessageItemData>(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), BaseRecyclerViewItemViewProvider<MessageItemDataType> {
    companion object {
        const val TAG = "MessageItemView"

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

    protected open lateinit var mBinding: ComponentMessageBinding
    protected var mTextView: MaterialTextView? = null

    init {
        val layoutInflater = LayoutInflater.from(context)

        loadVariables(context)
        inflate(layoutInflater)
        initAttrs(attrs)
    }

    protected open fun loadVariables(context: Context) {
        mBackgroundRight = AppCompatResources.getDrawable(context, BACKGROUND_DRAWABLE_RIGHT_RES_ID)!!
        mBackgroundLeft = AppCompatResources.getDrawable(context, BACKGROUND_DRAWABLE_LEFT_RES_ID)!!

        mBackgroundTintLeft = context.theme
            .resolveColorAttr(com.google.android.material.R.attr.colorSecondaryContainer)
        mBackgroundTintRight = context.theme
            .resolveColorAttr(com.google.android.material.R.attr.colorPrimaryContainer)

        mVerticalPadding = context.resources.getDimension(VERTICAL_PADDING_DIMEN_ID).toInt()
    }

    protected open fun inflate(layoutInflater: LayoutInflater) {
        mBinding = ComponentMessageBinding.inflate(layoutInflater, this)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        initLayoutAttrs()
    }

    protected open fun initLayoutAttrs() {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        orientation = VERTICAL

        updatePadding(top = mVerticalPadding, bottom = mVerticalPadding)
    }

    override fun setData(data: MessageItemDataType) {
        if (data.text != null) setText(data.text)

        mBinding.componentMessageTextTimestamp.text = data.timestamp

        changeLayoutBySenderSide(data.senderSide)
    }

    // todo: is it ok? search for the better way..
    private fun setText(text: String) {
        if (mTextView == null) mTextView = initTextView()

        mTextView!!.text = text
    }

    private fun initTextView(): MaterialTextView {
        val textView = mBinding.componentMessageTextStub.inflate() as MaterialTextView

        initTextViewLayoutParams(textView)

        return textView
    }

    protected open fun initTextViewLayoutParams(textView: MaterialTextView) {
        mBinding.componentMessageTextTimestamp.updateLayoutParams {
            this as ConstraintLayout.LayoutParams

            this.topToTop = ConstraintLayout.LayoutParams.UNSET
            this.topToBottom = textView.id
        }
    }

    private fun changeLayoutBySenderSide(senderSide: SenderSide) {
        changeLayoutConstraintsBySenderSide(senderSide)
        changeBackgroundBySenderSide(senderSide)
    }

    private fun changeLayoutConstraintsBySenderSide(senderSide: SenderSide) {
        mBinding.componentMessageContentWrapper.updateLayoutParams {
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

        mBinding.componentMessageContentWrapper.apply {
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

        mBinding.componentMessageContentWrapper.updateLayoutParams {
            this as LayoutParams

            this.width = getMinWidthByParentWidth(parentWidth)
        }
    }

    fun getMinWidthByParentWidth(parentWidth: Int): Int {
        return if (parentWidth > WIDTH_BREAKPOINT_PX) (parentWidth * MIN_WIDTH_PERCENT).toInt()
        else parentWidth
    }

    fun getContentWrapper(): ConstraintLayout {
        return mBinding.componentMessageContentWrapper
    }

    override fun getView(): View {
        return this
    }
}