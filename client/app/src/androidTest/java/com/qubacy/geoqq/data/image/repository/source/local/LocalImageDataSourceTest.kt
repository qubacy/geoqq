package com.qubacy.geoqq.data.image.repository.source.local

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.util.context.extension.checkUriValidity
import com.qubacy.geoqq.data.image.repository.source.local.entity.ImageEntity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalImageDataSourceTest {
    companion object {
        const val TEST_ICON_SIZE = 48
    }

    private lateinit var mContext: Context
    private lateinit var mTestRawImage: RawImage

    private lateinit var mLocalImageDataSource: LocalImageDataSource

    private val mImageEntitiesToDelete: MutableList<ImageEntity> = mutableListOf()

    @Before
    fun setup() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        initVars(mContext)
        initLocalImageDataSource(mContext)
    }

    @After
    fun clear() {
        for (imageToDelete in mImageEntitiesToDelete)
            mContext.contentResolver.delete(imageToDelete.uri, null, null)

        mImageEntitiesToDelete.clear()
    }

    private fun initVars(context: Context) {
        val drawable = context.getDrawable(R.drawable.ic_launcher_background)!!
        val bitmap = drawable.toBitmap(TEST_ICON_SIZE, TEST_ICON_SIZE)

        mTestRawImage = RawImage(0, Bitmap.CompressFormat.PNG, bitmap)
    }

    private fun initLocalImageDataSource(context: Context) {
        mLocalImageDataSource = LocalImageDataSource(context.contentResolver)
    }

    @Test
    fun saveImageTest() {
        val imageToSave = mTestRawImage

        val gottenImageEntity = mLocalImageDataSource.saveImage(imageToSave)

        Assert.assertNotNull(gottenImageEntity)

        mImageEntitiesToDelete.add(gottenImageEntity!!)

        Assert.assertEquals(imageToSave.id, gottenImageEntity.id)
        Assert.assertTrue(mContext.checkUriValidity(gottenImageEntity.uri))
    }

    @Test
    fun saveImagesTest() {
        val imagesToSave = listOf(mTestRawImage, mTestRawImage)

        val gottenImageEntities = mLocalImageDataSource.saveImages(imagesToSave)

        Assert.assertNotNull(gottenImageEntities)

        mImageEntitiesToDelete.addAll(gottenImageEntities!!)

        Assert.assertEquals(imagesToSave.size, gottenImageEntities.size)

        for (i in imagesToSave.indices) {
            val imageToSave = imagesToSave[i]
            val gottenImageEntity = gottenImageEntities[i]

            Assert.assertEquals(imageToSave.id, gottenImageEntity.id)
            Assert.assertTrue(mContext.checkUriValidity(gottenImageEntity.uri))
        }
    }

    @Test
    fun loadImageTest() {
        val initImage = mTestRawImage
        val expectedImageEntity = mLocalImageDataSource.saveImage(initImage)!!

        mImageEntitiesToDelete.add(expectedImageEntity)

        val gottenImageEntity = mLocalImageDataSource.loadImage(expectedImageEntity.id)

        Assert.assertEquals(expectedImageEntity, gottenImageEntity)
    }

    @Test
    fun loadImagesTest() {
        val initImages = listOf(mTestRawImage, mTestRawImage)
        val expectedImageEntities = mLocalImageDataSource.saveImages(initImages)!!

        mImageEntitiesToDelete.addAll(expectedImageEntities)

        val gottenImageEntities = mLocalImageDataSource
            .loadImages(expectedImageEntities.map { it.id })

        Assert.assertNotNull(gottenImageEntities)
        Assert.assertEquals(expectedImageEntities.size, gottenImageEntities!!.size)

        for (expectedImageEntity in expectedImageEntities)
            Assert.assertTrue(gottenImageEntities.contains(expectedImageEntity))
    }

    @Test
    fun getImageDataByUriTest() {
        val expectedImageData = mTestRawImage
        val initImageEntity = mLocalImageDataSource.saveImage(expectedImageData)!!

        mImageEntitiesToDelete.add(initImageEntity)

        val gottenImageData = mLocalImageDataSource.getImageDataByUri(initImageEntity.uri)!!
            .copy(id = expectedImageData.id)

        Assert.assertEquals(expectedImageData, gottenImageData)
    }
}