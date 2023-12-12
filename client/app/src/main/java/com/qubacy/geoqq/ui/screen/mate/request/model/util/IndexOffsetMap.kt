package com.qubacy.geoqq.ui.screen.mate.request.model.util

class IndexOffsetMap {
    data class IndexOffsetItem(
        var index: Int,
        var offset: Int
    )

    private val mIndexOffsetList: MutableList<IndexOffsetItem> = mutableListOf()

    init { }

    private fun mergeNeighborsForIndexOffsetItem(curIndexOffsetItem: IndexOffsetItem) {
        var prevNeighborIndexOffsetItem: IndexOffsetItem? = null
        var nextNeighborIndexOffsetItem: IndexOffsetItem? = null

        var isIndexPassed = false

        for (indexOffsetItem in mIndexOffsetList) {
            if (indexOffsetItem.index + indexOffsetItem.offset == curIndexOffsetItem.index) {
                prevNeighborIndexOffsetItem = indexOffsetItem

            } else if (indexOffsetItem.index == curIndexOffsetItem.index) {

                isIndexPassed = true

            } else if (isIndexPassed) {
                if (indexOffsetItem.index - 1 == curIndexOffsetItem.index)
                    nextNeighborIndexOffsetItem = indexOffsetItem

                break
            }
        }

        if (prevNeighborIndexOffsetItem != null) {
            if (nextNeighborIndexOffsetItem != null) {
                prevNeighborIndexOffsetItem.offset +=
                    (curIndexOffsetItem.offset + nextNeighborIndexOffsetItem.offset)

                mIndexOffsetList.remove(nextNeighborIndexOffsetItem)

            } else {
                prevNeighborIndexOffsetItem.offset += curIndexOffsetItem.offset
            }

            mIndexOffsetList.remove(curIndexOffsetItem)

        } else if (nextNeighborIndexOffsetItem != null) {
            curIndexOffsetItem.offset += nextNeighborIndexOffsetItem.offset

            mIndexOffsetList.remove(nextNeighborIndexOffsetItem)
        }
    }

    private fun renewIndicesFromIndex(index: Int) {
        for (indexOffsetItem in mIndexOffsetList) {
            if (indexOffsetItem.index <= index) continue

            indexOffsetItem.index--
        }
    }

    fun addIndex(index: Int) {
        if (mIndexOffsetList.isEmpty()) {
            mIndexOffsetList.add(IndexOffsetItem(index, 1))

            return
        }

        val indexOffsetItemForIndex = mIndexOffsetList.find { it.index == index }

        if (indexOffsetItemForIndex != null) {
            indexOffsetItemForIndex.offset++

            renewIndicesFromIndex(index)
            return mergeNeighborsForIndexOffsetItem(indexOffsetItemForIndex)
        }

        val prevProbableIndexOffsetItem = mIndexOffsetList.find { it.index == index - 1 }

        if (prevProbableIndexOffsetItem != null) {
            prevProbableIndexOffsetItem.offset++

            renewIndicesFromIndex(index)
            return mergeNeighborsForIndexOffsetItem(prevProbableIndexOffsetItem)
        }

        val nextProbableIndexOffsetItem = mIndexOffsetList.find { it.index == index + 1 }

        if (nextProbableIndexOffsetItem != null) {
            nextProbableIndexOffsetItem.index = index
            nextProbableIndexOffsetItem.offset++

            renewIndicesFromIndex(index)
            return mergeNeighborsForIndexOffsetItem(nextProbableIndexOffsetItem)
        }

        val nextIndexOffsetItemIndex = mIndexOffsetList.indexOfFirst { it.index > index }

        val curIndexOffsetItem = if (nextIndexOffsetItemIndex == -1) {
            mIndexOffsetList.add(IndexOffsetItem(index, 1))
            mIndexOffsetList.last()
        } else {
            val newIndexOffsetItem = IndexOffsetItem(index, 1)

            mIndexOffsetList.add(nextIndexOffsetItemIndex, newIndexOffsetItem)
            newIndexOffsetItem
        }

        renewIndicesFromIndex(index)
        mergeNeighborsForIndexOffsetItem(curIndexOffsetItem)
    }

    fun getIndexWithOffset(index: Int): Int {
        var indexOffsetAccumulator = 0

        for (indexOffsetItem in mIndexOffsetList) {
            if (indexOffsetItem.index > index) break

            indexOffsetAccumulator += indexOffsetItem.offset
        }

        return (index + indexOffsetAccumulator)
    }
}