package com.qubacy.geoqq._common._test.util.assertion

import org.junit.Assert

object AssertUtils {
    fun assertEqualContent(expectedList: List<*>, gottenList: List<*>) {
        Assert.assertEquals(expectedList.size, gottenList.size)

        for (expectedItem in expectedList)
            Assert.assertTrue(gottenList.contains(expectedItem))
    }
}