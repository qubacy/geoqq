package com.qubacy.geoqq.data.image.repository._common._test.context

import android.graphics.Bitmap
import com.qubacy.geoqq._common._test.util.mock.BitmapMockUtil
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.entity.ImageEntity
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImageResponse

object ImageDataRepositoryTestContext {
    const val DEFAULT_IMAGE_ID = 0L
    const val DEFAULT_IMAGE_BASE64_CONTENT = ""
    val DEFAULT_IMAGE_BITMAP = BitmapMockUtil.mockBitmap()
    val DEFAULT_IMAGE_EXTENSION = ImageExtension.PNG_EXTENSION.id
    val DEFAULT_IMAGE_URI = UriMockUtil.getMockedUri()

    val DEFAULT_IMAGE_ENTITY = ImageEntity(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_URI)
    val DEFAULT_RAW_IMAGE = RawImage(
        extension = Bitmap.CompressFormat.PNG, content = DEFAULT_IMAGE_BITMAP)
    val DEFAULT_GET_IMAGE_RESPONSE = GetImageResponse(
        DEFAULT_IMAGE_ID, DEFAULT_IMAGE_EXTENSION, DEFAULT_IMAGE_BASE64_CONTENT)

    val DEFAULT_DATA_IMAGE = DataImage(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_URI)
}