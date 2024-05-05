package com.qubacy.geoqq.data.image.repository

import android.graphics.Bitmap
import android.net.Uri
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.BitmapFactoryMockUtil
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.model.toDataImage
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.UploadImageResponse
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.local.entity.ImageEntity
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ImageDataRepositoryTest(

) : DataRepositoryTest<ImageDataRepository>() {
    companion object {
        init {
            BitmapFactoryMockUtil.mockBitmapFactory()
        }
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataSourceMockContainer

    private var mLocalSourceLoadImage: ImageEntity? = null
    private var mLocalSourceLoadImages: List<ImageEntity>? = null
    private var mLocalSourceSaveImage: ImageEntity? = null
    private var mLocalSourceSaveImages: List<ImageEntity>? = null
    private var mLocalSourceGetImageDataByUri: RawImage? = null

    private var mLocalSourceLoadImageCallFlag = false
    private var mLocalSourceLoadImagesCallFlag = false
    private var mLocalSourceSaveImageCallFlag = false
    private var mLocalSourceSaveImagesCallFlag = false
    private var mLocalSourceGetImageDataByUriCallFlag = false

    private var mHttpSourceGetImageResponse: GetImageResponse? = null
    private var mHttpSourceGetImagesResponse: GetImagesResponse? = null
    private var mHttpSourceUploadImageResponse: UploadImageResponse? = null

    private var mHttpSourceGetImageCallFlag = false
    private var mHttpSourceGetImagesCallFlag = false
    private var mHttpSourceUploadImageCallFlag = false

    @Before
    fun setup() {
        initImageDataRepository()
    }

    @After
    fun clear() {
        mLocalSourceLoadImage = null
        mLocalSourceLoadImages = null
        mLocalSourceSaveImage = null
        mLocalSourceSaveImages = null
        mLocalSourceGetImageDataByUri = null

        mLocalSourceLoadImageCallFlag = false
        mLocalSourceLoadImagesCallFlag = false
        mLocalSourceSaveImageCallFlag = false
        mLocalSourceSaveImagesCallFlag = false
        mLocalSourceGetImageDataByUriCallFlag = false

        mHttpSourceGetImageResponse = null
        mHttpSourceGetImagesResponse = null
        mHttpSourceUploadImageResponse = null

        mHttpSourceGetImageCallFlag = false
        mHttpSourceGetImagesCallFlag = false
        mHttpSourceUploadImageCallFlag = false
    }

    private fun initImageDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()

        val localImageDataSourceMock = mockLocalImageDataSource()
        val httpImageDataSourceMock = mockHttpImageDataSource()

        mDataRepository = ImageDataRepository(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            localImageDataSourceMock,
            httpImageDataSourceMock
        )
    }

    private fun mockLocalImageDataSource(): LocalImageDataSource {
        val localImageDataSourceMock = Mockito.mock(LocalImageDataSource::class.java)

        Mockito.`when`(localImageDataSourceMock.loadImage(Mockito.anyLong())).thenAnswer {
            mLocalSourceLoadImageCallFlag = true
            mLocalSourceLoadImage
        }
        Mockito.`when`(localImageDataSourceMock.loadImages(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceLoadImagesCallFlag = true
            mLocalSourceLoadImages
        }
        Mockito.`when`(localImageDataSourceMock.saveImage(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceSaveImageCallFlag = true
            mLocalSourceSaveImage
        }
        Mockito.`when`(localImageDataSourceMock.saveImages(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceSaveImagesCallFlag = true
            mLocalSourceSaveImages
        }
        Mockito.`when`(localImageDataSourceMock.getImageDataByUri(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceGetImageDataByUriCallFlag = true
            mLocalSourceGetImageDataByUri
        }

        return localImageDataSourceMock
    }

    private fun mockHttpImageDataSource(): HttpImageDataSource {
        val httpImageDataSourceMock = Mockito.mock(HttpImageDataSource::class.java)

        Mockito.`when`(httpImageDataSourceMock.getImage(
            Mockito.anyLong()
        )).thenAnswer {
            mHttpSourceGetImageCallFlag = true
            mHttpSourceGetImageResponse
        }
        Mockito.`when`(httpImageDataSourceMock.getImages(AnyMockUtil.anyObject())).thenAnswer {
            mHttpSourceGetImagesCallFlag = true
            mHttpSourceGetImagesResponse
        }
        Mockito.`when`(httpImageDataSourceMock.uploadImage(
            Mockito.anyInt(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceUploadImageCallFlag = true
            mHttpSourceUploadImageResponse
        }

        return httpImageDataSourceMock
    }

    @Test
    fun getImageByIdFromLocalSourceTest() = runTest {
        val imageId = 0L
        val uriMock = Mockito.mock(Uri::class.java)

        val loadImage = ImageEntity(imageId, uriMock)

        mLocalSourceLoadImage = loadImage

        val expectedDataImage = DataImage(imageId, uriMock)

        val gottenDataImage = mDataRepository.getImageById(expectedDataImage.id)

        Assert.assertTrue(mLocalSourceLoadImageCallFlag)
        Assert.assertEquals(expectedDataImage, gottenDataImage)
    }

    @Test
    fun getImageByIdFromHttpSourceTest() = runTest {
        val imageId = 0L
        val extension = ImageExtension.PNG_EXTENSION.id
        val base64Content = ""
        val uriMock = Mockito.mock(Uri::class.java)

        val getImageResponse = GetImageResponse(imageId, extension, base64Content)
        val saveImage = ImageEntity(imageId, uriMock)

        mHttpSourceGetImageResponse = getImageResponse
        mLocalSourceSaveImage = saveImage

        val expectedDataImage = DataImage(0, uriMock)

        val gottenDataImage = mDataRepository.getImageById(expectedDataImage.id)

        Assert.assertTrue(mLocalSourceLoadImageCallFlag)
        Assert.assertTrue(mHttpSourceGetImageCallFlag)
        Assert.assertTrue(mLocalSourceSaveImageCallFlag)
        Assert.assertEquals(expectedDataImage, gottenDataImage)
    }

    @Test
    fun getImagesByIdsFromLocalSourceTest() = runTest {
        val loadImages = generateImageEntities(2)

        mLocalSourceLoadImages = loadImages

        val expectedDataImages = loadImages.map { it.toDataImage() }

        val gottenDataImages = mDataRepository.getImagesByIds(expectedDataImages.map { it.id })

        Assert.assertTrue(mLocalSourceLoadImagesCallFlag)
        AssertUtils.assertEqualContent(expectedDataImages, gottenDataImages)
    }

    @Test
    fun getImagesByIdsFromHttpSourceTest() = runTest {
        val getImagesResponse = GetImagesResponse(generateGetImageResponse(
            2, extension = ImageExtension.PNG_EXTENSION.id))
        val saveImages = generateImageEntities(getImagesResponse.images.size)

        mHttpSourceGetImagesResponse = getImagesResponse
        mLocalSourceSaveImages = saveImages

        val expectedDataImages = saveImages.map { it.toDataImage() }

        val gottenDataImages = mDataRepository.getImagesByIds(expectedDataImages.map { it.id })

        Assert.assertTrue(mLocalSourceLoadImagesCallFlag)
        Assert.assertTrue(mHttpSourceGetImagesCallFlag)
        Assert.assertTrue(mLocalSourceSaveImagesCallFlag)
        Assert.assertEquals(expectedDataImages, gottenDataImages)
    }

    @Test
    fun saveImageTest() = runTest {
        val bitmapMock = Mockito.mock(Bitmap::class.java)

        Mockito.`when`(bitmapMock.compress(
            AnyMockUtil.anyObject(), Mockito.anyInt(), AnyMockUtil.anyObject()
        )).thenAnswer { true }

        val id = 0L
        val uriMock = Mockito.mock(Uri::class.java)
        val getImageDataByUri = RawImage(
            extension = Bitmap.CompressFormat.PNG, content = bitmapMock)
        val uploadImageResponse = UploadImageResponse(id)
        val saveImage = ImageEntity(id, uriMock)
        val expectedDataImage = saveImage.toDataImage()

        mLocalSourceGetImageDataByUri = getImageDataByUri
        mHttpSourceUploadImageResponse = uploadImageResponse
        mLocalSourceSaveImage = saveImage

        val gottenDataImage = mDataRepository.saveImage(uriMock)

        Assert.assertTrue(mLocalSourceGetImageDataByUriCallFlag)
        Assert.assertTrue(mHttpSourceUploadImageCallFlag)
        Assert.assertTrue(mLocalSourceSaveImageCallFlag)
        Assert.assertEquals(expectedDataImage, gottenDataImage)
    }

    private fun generateImageEntities(
        count: Int,
        uri: Uri = Mockito.mock(Uri::class.java)
    ): List<ImageEntity> {
        return IntRange(0, count - 1).map {
            ImageEntity(it.toLong(), uri)
        }
    }

    private fun generateGetImageResponse(
        count: Int,
        extension: Int = ImageExtension.PNG_EXTENSION.id,
        content: String = String()
    ): List<GetImageResponse> {
        return IntRange(0, count - 1).map {
            GetImageResponse(it.toLong(), extension, content)
        }
    }
}