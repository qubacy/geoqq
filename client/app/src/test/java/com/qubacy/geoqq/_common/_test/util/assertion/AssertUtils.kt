package com.qubacy.geoqq._common._test.util.assertion

import org.junit.Assert

object AssertUtils {
    fun assertEqualContent(expectedList: List<*>, gottenList: List<*>) {
        Assert.assertEquals(expectedList.size, gottenList.size)

        for (expectedItem in expectedList)
            Assert.assertTrue(gottenList.contains(expectedItem))
    }

    fun assertEqualMaps(expectedMap: Map<*, *>, gottenMap: Map<*, *>) {
        Assert.assertEquals(expectedMap.size, gottenMap.size)

        for (expectedEntry in expectedMap) {
            Assert.assertTrue(gottenMap.contains(expectedEntry.key))
            Assert.assertEquals(expectedEntry.value, gottenMap[expectedEntry.key])
        }
    }
}