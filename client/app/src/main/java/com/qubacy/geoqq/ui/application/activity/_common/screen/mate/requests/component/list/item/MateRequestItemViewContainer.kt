package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qubacy.geoqq.databinding.ComponentMateRequestPreviewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestItemViewContainer(
    parent: ViewGroup
) : RecyclerViewItemView<MateRequestItemData> {
    private lateinit var mBinding: ComponentMateRequestPreviewBinding

    init {
        inflate(parent)
    }

    private fun inflate(parent: ViewGroup) {
        val layoutInflater = LayoutInflater.from(parent.context)

        mBinding = ComponentMateRequestPreviewBinding
            .inflate(layoutInflater, parent, false)
    }

    override fun setData(data: MateRequestItemData) {
        mBinding.componentMateRequestPreviewImageAvatar.setImageURI(data.imageUri)
        mBinding.componentMateRequestPreviewTextUsername.text = data.username
    }
}