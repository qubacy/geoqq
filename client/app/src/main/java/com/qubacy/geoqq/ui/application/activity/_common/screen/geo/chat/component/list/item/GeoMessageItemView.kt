package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.textview.MaterialTextView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData

class GeoMessageItemView(
    context: Context,
    attributeSet: AttributeSet? = null
) : MessageItemView<GeoMessageItemData>(context, attributeSet) {
    companion object {
        val USERNAME_CONTENT_GAP_ID = R.dimen.tiny_gap_between_components
    }

    private lateinit var mUsernameView: MaterialTextView

    private var mUsernameContentGap: Int? = null

    override fun loadVariables(context: Context) {
        super.loadVariables(context)

        mUsernameContentGap = context.resources.getDimension(USERNAME_CONTENT_GAP_ID).toInt()
    }

    override fun inflate(layoutInflater: LayoutInflater) {
        super.inflate(layoutInflater)

        mUsernameView = layoutInflater.inflate(
            R.layout.component_geo_message_username,
            mBinding.componentMessageContentWrapper,
            false
        ) as MaterialTextView

        mBinding.componentMessageContentWrapper.addView(mUsernameView, 0)
    }

    override fun initLayoutAttrs() {
        super.initLayoutAttrs()

        mUsernameView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }
        mBinding.componentMessageTextTimestamp.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.component_geo_message_text_username
        }
    }

    override fun initTextViewLayoutParams(textView: MaterialTextView) {
        super.initTextViewLayoutParams(textView)

        textView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.UNSET
            topToBottom = R.id.component_geo_message_text_username
            topMargin = mUsernameContentGap!!
        }
    }

    override fun setData(data: GeoMessageItemData) {
        super.setData(data)

        mUsernameView.text = data.username
    }
}