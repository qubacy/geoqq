package com.qubacy.geoqq.data._common.util.hasher

import org.junit.Assert
import org.junit.Test

class HasherUtilTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun hashStringTest() {
        class TestCase(
            val algorithm: HasherUtil.HashAlgorithm,
            val string: String,
            val expectedBytes: ByteArray
        )

        val testCases = listOf(
            TestCase(
                HasherUtil.HashAlgorithm.SHA256,
                "test",
                "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08".hexToByteArray()
            ),
            TestCase(
                HasherUtil.HashAlgorithm.SHA256,
                "test 2",
                "dec2e4bc4992314a9c9a51bbd859e1b081b74178818c53c19d18d6f761f5d804".hexToByteArray()
            ),
        )

        for (testCase in testCases) {
            val gottenBytes = HasherUtil.hashString(testCase.string, testCase.algorithm)

            Assert.assertArrayEquals(testCase.expectedBytes, gottenBytes)
        }
    }
}