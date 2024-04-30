package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.textview.MaterialTextView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData

class GeoMessageItemView(
    context: Context,
    attributeSet: AttributeSet? = null
) : MessageItemView<GeoMessageItemData>(context, attributeSet) {
    private lateinit var mUsernameView: MaterialTextView

    override fun inflate(layoutInflater: LayoutInflater) {
        super.inflate(layoutInflater)

        mUsernameView = layoutInflater.inflate(
            R.layout.component_geo_message_username,
            mBinding.componentMateMessageContentWrapper,
            false
        ) as MaterialTextView

        mBinding.componentMateMessageContentWrapper.addView(mUsernameView, 0)
    }

    override fun setData(data: GeoMessageItemData) {
        super.setData(data)

        mUsernameView.text = data.username
    }
}