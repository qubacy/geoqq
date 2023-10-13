package com.qubacy.geoqq.ui.common.component.bottomsheet

interface BottomSheetContent<DataType> {
    fun setCallback(callback: BottomSheetContentCallback)
    fun setData(data: DataType)
    fun showPreview()
    fun close()
}