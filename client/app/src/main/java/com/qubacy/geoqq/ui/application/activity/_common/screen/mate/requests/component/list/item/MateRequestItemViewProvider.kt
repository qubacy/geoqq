package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qubacy.choosablelistviewlib.item.content.ChoosableItemContentViewProvider
import com.qubacy.geoqq.databinding.ComponentMateRequestPreviewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestItemViewProvider(
    parent: ViewGroup
) : ChoosableItemContentViewProvider<MateRequestItemData> {
    private lateinit var mBinding: ComponentMateRequestPreviewBinding

    init {
        inflate(parent)
    }

    private fun inflate(parent: ViewGroup) {
        val layoutInflater = LayoutInflater.from(parent.context)

        mBinding = ComponentMateRequestPreviewBinding
            .inflate(layoutInflater, parent, false)
    }

    override fun setData(contentItemData: MateRequestItemData) {
        mBinding.componentMateRequestPreviewImageAvatar.setImageURI(contentItemData.imageUri)
        mBinding.componentMateRequestPreviewTextUsername.text = contentItemData.username
    }

    override fun getView(): View {
        return mBinding.root
    }
}