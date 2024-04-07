package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.adapter

import android.view.View
import android.view.ViewGroup
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.BaseRecyclerViewAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.producer.BaseItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.data.RecyclerViewItemData
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

abstract class BaseRecyclerViewAdapterTest<
    RecyclerViewItemDataType : RecyclerViewItemData,
    RecyclerViewItemViewType,
    RecyclerViewItemViewProducerType: BaseItemViewProducer<
            RecyclerViewItemDataType, RecyclerViewItemViewType
            >,
    ViewHolderType : BaseRecyclerViewAdapter.ViewHolder<
        RecyclerViewItemDataType, RecyclerViewItemViewType
    >,
    AdapterType : BaseRecyclerViewAdapter<
            RecyclerViewItemDataType, RecyclerViewItemViewType,
            RecyclerViewItemViewProducerType, ViewHolderType
            >
>(

) where RecyclerViewItemViewType : RecyclerViewItemView<RecyclerViewItemDataType>,
        RecyclerViewItemViewType : View
{
    protected lateinit var mItemViewProducer: RecyclerViewItemViewProducerType
    protected lateinit var mViewHolder: ViewHolderType
    protected lateinit var mAdapter: AdapterType

    protected var mItemViewProducerCreateItemViewCallFlag: Boolean = false
    protected var mCreateViewHolderCallFlag: Boolean = false
    protected var mViewHolderSetDataCallFlag: Boolean = false

    @Before
    open fun setup() {
        init()
    }

    @After
    open fun clear() {
        mItemViewProducerCreateItemViewCallFlag = false
        mCreateViewHolderCallFlag = false
        mViewHolderSetDataCallFlag = false
    }

    private fun init() {
        initItemViewProducer()
        initViewHolder()
        initAdapter()
    }

    private fun initItemViewProducer() {
        mItemViewProducer = createItemViewProducerMock()
    }

    // todo: check this again:
    protected open fun createItemViewProducerMock(): RecyclerViewItemViewProducerType {
        val itemViewProducerMock = Mockito.mock(BaseItemViewProducer::class.java)

        Mockito.`when`(itemViewProducerMock.createItemView(
            AnyMockUtil.anyObject(), Mockito.anyInt()
        )).thenAnswer {
            mItemViewProducerCreateItemViewCallFlag = true

            createItemViewMock()
        }

        return itemViewProducerMock as RecyclerViewItemViewProducerType
    }

    protected abstract fun createItemViewMock(): RecyclerViewItemViewType

    private fun initViewHolder() {
        val viewHolderMock = createViewHolderMock()

        Mockito.`when`(viewHolderMock.setData(AnyMockUtil.anyObject()))
            .thenAnswer {
                mViewHolderSetDataCallFlag = true

                Unit
            }

        mViewHolder = viewHolderMock
    }

    private fun initAdapter() {
        val adapter = createAdapter(mItemViewProducer)
        val spiedAdapter = Mockito.spy(adapter)

        spyAdapter(spiedAdapter)

        mAdapter = spiedAdapter
    }

    protected abstract fun createAdapter(
        itemViewProducer: RecyclerViewItemViewProducerType
    ): AdapterType

    protected open fun spyAdapter(spiedAdapter: AdapterType) {
        Mockito.doAnswer {
            mCreateViewHolderCallFlag = true

            mViewHolder
        }.`when`(spiedAdapter).createViewHolder(AnyMockUtil.anyObject())

        Mockito.doAnswer{ }.`when`(spiedAdapter).wrappedNotifyDataSetChanged()
        Mockito.doAnswer{ }.`when`(spiedAdapter).wrappedNotifyItemInserted(Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter)
            .wrappedNotifyItemRangeInserted(Mockito.anyInt(), Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter).wrappedNotifyItemChanged(Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter)
            .wrappedNotifyItemRangeChanged(Mockito.anyInt(), Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter)
            .wrappedNotifyItemMoved(Mockito.anyInt(), Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter).wrappedNotifyItemRemoved(Mockito.anyInt())
        Mockito.doAnswer{ }.`when`(spiedAdapter)
            .wrappedNotifyItemRangeRemoved(Mockito.anyInt(), Mockito.anyInt())
    }

    protected abstract fun createViewHolderMock(): ViewHolderType

    @Test
    open fun onCreateViewHolderTest() {
        val parentMock = Mockito.mock(ViewGroup::class.java)
        val viewType = 0

        mAdapter.onCreateViewHolder(parentMock, viewType)

        Assert.assertTrue(mItemViewProducerCreateItemViewCallFlag)
        Assert.assertTrue(mCreateViewHolderCallFlag)
    }

    @Test
    open fun onBindViewHolderTest() {
        val position = 0

        mAdapter.onBindViewHolder(mViewHolder, position)

        Assert.assertTrue(mViewHolderSetDataCallFlag)
    }

    @Test
    fun getItemCountTest() {
        Assert.assertEquals(0, mAdapter.itemCount)

        val items = getTestItems(3)

        setItemsToAdapter(items)

        Assert.assertEquals(items.size, mAdapter.itemCount)
    }

    @Test
    fun resetItemsTest() {
        val initItems = getTestItems(2)

        setItemsToAdapter(initItems)

        mAdapter.resetItems()

        Assert.assertTrue(mAdapter.items.isEmpty())
    }

    protected abstract fun getTestItems(
        count: Int, offset: Int = 0
    ): List<RecyclerViewItemDataType>

    protected fun setItemsToAdapter(items: List<RecyclerViewItemDataType>) {
        BaseRecyclerViewAdapter::class.java.getDeclaredField("mItems")
            .apply {
                isAccessible = true

                set(mAdapter, items.toMutableList())
            }
    }
}