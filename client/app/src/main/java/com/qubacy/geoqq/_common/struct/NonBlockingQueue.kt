package com.qubacy.geoqq._common.struct

import java.util.LinkedList

class NonBlockingQueue<ItemType> {
    private val mList: LinkedList<ItemType> = LinkedList()

    @Synchronized
    fun enqueue(item: ItemType) {
        mList.add(item)
    }

    @Synchronized
    fun dequeue(): ItemType? {
        return if (mList.isNotEmpty()) mList.removeFirst()
        else null
    }
}