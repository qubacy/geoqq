package com.qubacy.geoqq.ui.screen.mate.request.model.util

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IndexOffsetMapTest {
    private lateinit var mIndexOffsetList: List<IndexOffsetMap.IndexOffsetItem>
    private lateinit var mIndexOffsetMap: IndexOffsetMap

    @Before
    fun setup() {
        val mIndexOffsetListFieldReflection = IndexOffsetMap::class.java
            .getDeclaredField("mIndexOffsetList").apply { isAccessible = true }

        mIndexOffsetMap = IndexOffsetMap()
        mIndexOffsetList = mIndexOffsetListFieldReflection.get(mIndexOffsetMap)
                as List<IndexOffsetMap.IndexOffsetItem>
    }

    @Test
    fun addingTwoIndicesTest() {
        mIndexOffsetMap.addIndex(1)
        mIndexOffsetMap.addIndex(3)

        Assert.assertEquals(0, mIndexOffsetMap.getIndexWithOffset(0))
        Assert.assertEquals(2, mIndexOffsetMap.getIndexWithOffset(1))
        Assert.assertEquals(3, mIndexOffsetMap.getIndexWithOffset(2))
        Assert.assertEquals(10, mIndexOffsetMap.getIndexWithOffset(8))

        mIndexOffsetMap.addIndex(8)

        Assert.assertEquals(7, mIndexOffsetMap.getIndexWithOffset(5))
        Assert.assertEquals(11, mIndexOffsetMap.getIndexWithOffset(8))

        mIndexOffsetMap.addIndex(5)

        Assert.assertEquals(11, mIndexOffsetMap.getIndexWithOffset(7))
    }

    @Test
    fun mergingNeighborIndexOffsetItemsTest() {
        mIndexOffsetMap.addIndex(0)
        mIndexOffsetMap.addIndex(1)

        var expectedIndexOffsetItem = IndexOffsetMap.IndexOffsetItem(0, 2)

        Assert.assertEquals(1, mIndexOffsetList.size)
        Assert.assertEquals(expectedIndexOffsetItem, mIndexOffsetList.first())

        mIndexOffsetMap.addIndex(3)
        mIndexOffsetMap.addIndex(3)
        mIndexOffsetMap.addIndex(4)

        expectedIndexOffsetItem = IndexOffsetMap.IndexOffsetItem(3, 3)

        Assert.assertEquals(2, mIndexOffsetList.size)
        Assert.assertEquals(expectedIndexOffsetItem, mIndexOffsetList.last())

        mIndexOffsetMap.addIndex(2)

        expectedIndexOffsetItem = IndexOffsetMap.IndexOffsetItem(0, 6)

        Assert.assertEquals(1, mIndexOffsetList.size)
        Assert.assertEquals(expectedIndexOffsetItem, mIndexOffsetList.first())

        mIndexOffsetMap.addIndex(10)
        mIndexOffsetMap.addIndex(5)
        mIndexOffsetMap.addIndex(5)

        expectedIndexOffsetItem = IndexOffsetMap.IndexOffsetItem(8, 1)

        Assert.assertEquals(3, mIndexOffsetList.size)
        Assert.assertEquals(expectedIndexOffsetItem, mIndexOffsetList.last())
    }
}