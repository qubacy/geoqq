package com.qubacy.geoqq.domain.common.usecase.util.extension.image

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.result.GetImageUriResult

interface ImageExtension {
    suspend fun getImageUri(
        imageId: Long,
        accessToken: String,
        imageDataRepository: ImageDataRepository
    ): Result {
        val getImageResult = imageDataRepository.getImage(imageId, accessToken)

        if (getImageResult is ErrorResult) return getImageResult

        val getImageResultCast = getImageResult as GetImageResult

        return GetImageUriResult(getImageResultCast.imageUri)
    }
}