package com.qubacy.geoqq.ui.common.visual.component.bottomsheet.common.content

interface BottomSheetContent<DataType> {
    fun setCallback(callback: BottomSheetContentCallback)
    fun setData(data: DataType)
    fun showPreview()
    fun close()
}