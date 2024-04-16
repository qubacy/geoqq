package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentMateChatPreviewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData
import kotlin.math.min

class MateChatItemView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), RecyclerViewItemViewProvider<MateChatItemData> {
    companion object {
        const val MAX_NEW_MESSAGE_COUNT_TO_DISPLAY = 100
    }

    private lateinit var mBinding: ComponentMateChatPreviewBinding

    init {
        inflate()
        initAttrs(attrs)
    }

    private fun inflate() {
        val layoutInflater = LayoutInflater.from(context)

        mBinding = ComponentMateChatPreviewBinding.inflate(layoutInflater, this)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        initLayoutAttrs()
    }

    private fun initLayoutAttrs() {
        val startPadding = context.resources
            .getDimension(R.dimen.small_gap_between_components).toInt()
        val verticalPadding = context.resources
            .getDimension(R.dimen.very_small_gap_between_components).toInt()
        val endPadding = context.resources
            .getDimension(R.dimen.medium_gap_between_components).toInt()

        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        updatePadding(startPadding, verticalPadding, endPadding, verticalPadding)
    }

    override fun setData(data: MateChatItemData) {
        mBinding.componentMateChatPreviewImage.setImageURI(data.imageUri)
        mBinding.componentMateChatPreviewTitle.text = data.title
        mBinding.componentMateChatPreviewText.text = data.text
        mBinding.componentMateChatPreviewUpdateCount.text =
            generateUpdateCountText(data.newMessageCount)
    }

    private fun generateUpdateCountText(updateCount: Int): String {
        return if (updateCount <= 0) String()
        else "+${min(updateCount, MAX_NEW_MESSAGE_COUNT_TO_DISPLAY)}"
    }

    override fun getView(): View {
        return this
    }
}