package com.qubacy.geoqq._common._test.util.turbine.extension

import app.cash.turbine.TurbineTestContext
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <ItemType>TurbineTestContext<*>.awaitAllItems(): List<ItemType> {
    val channel = this.asChannel()
    val items = mutableListOf<ItemType>()

    while (!channel.isEmpty) {
        items.add(channel.receive() as ItemType)
    }

    return items
}