package com.qubacy.geoqq.data.image.repository._test.mock

import android.net.Uri
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository.impl.ImageDataRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class ImageDataRepositoryMockContainer {
    companion object {
        val DEFAULT_DATA_IMAGE = DataImage(0L, Mockito.mock(Uri::class.java))
    }

    val imageDataRepositoryMock: ImageDataRepositoryImpl

    var getImageById: DataImage? = DEFAULT_DATA_IMAGE
    var getImagesByIds: List<DataImage>? = listOf(DEFAULT_DATA_IMAGE)
    var saveImage: DataImage? = DEFAULT_DATA_IMAGE

    private var mGetImageByIdCallFlag = false
    val getImageByIdCallFlag get() = mGetImageByIdCallFlag
    private var mGetImagesByIdsCallFlag = false
    val getImagesByIdsCallFlag get() = mGetImagesByIdsCallFlag
    private var mSaveImageCallFlag = false
    val saveImageCallFlag get() = mSaveImageCallFlag

    init {
        imageDataRepositoryMock = mockImageDataRepository()
    }

    private fun mockImageDataRepository(): ImageDataRepositoryImpl {
        val imageDataRepositoryMock = Mockito.mock(ImageDataRepositoryImpl::class.java)

        runTest {
            Mockito.`when`(imageDataRepositoryMock.getImageById(Mockito.anyLong())).thenAnswer {
                mGetImageByIdCallFlag = true
                getImageById!!
            }
            Mockito.`when`(imageDataRepositoryMock.getImagesByIds(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mGetImagesByIdsCallFlag = true
                getImagesByIds!!
            }
            Mockito.`when`(imageDataRepositoryMock.saveImage(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mSaveImageCallFlag = true
                saveImage!!
            }
        }

        return imageDataRepositoryMock
    }

    fun reset() {
        getImageById = DEFAULT_DATA_IMAGE
        getImagesByIds = listOf(DEFAULT_DATA_IMAGE)
        saveImage = DEFAULT_DATA_IMAGE

        mGetImageByIdCallFlag = false
        mGetImagesByIdsCallFlag = false
        mSaveImageCallFlag = false
    }
}