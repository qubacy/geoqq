package com.qubacy.geoqq.ui.screen.geochat.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.databinding.ComponentChatMessageBinding
import com.qubacy.geoqq.ui.common.util.TimeUtils
import com.qubacy.geoqq.ui.screen.geochat.chat.animator.ChatMessageAnimatorCallback
import com.qubacy.geoqq.ui.screen.geochat.chat.layoutmanager.GeoChatLayoutManager
import java.util.Locale
import java.util.TimeZone

data class MessageAdapterInfo(
    val message: Message,
    var wasAnimated: Boolean = false
)

class GeoChatAdapter(
    private val mCallback: GeoChatAdapterCallback
) : RecyclerView.Adapter<GeoChatAdapter.GeoChatViewHolder>(), ChatMessageAnimatorCallback {
    class GeoChatViewHolder(
        private val mBinding: ComponentChatMessageBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(message: Message, user: User) {
            mBinding.text.text = message.text
            mBinding.username.text = user.username
            mBinding.timestamp.text = TimeUtils.longToHoursMinutesSecondsFormattedString(
                message.timestamp, Locale.getDefault(), TimeZone.getDefault()) // todo: is it OK to do it here??
        }
    }

    private val mMessageAdapterInfoList = mutableListOf<MessageAdapterInfo>()

    private var _mIsAutoScrollingEnabled: Boolean = true
    private val mIsAutoScrollingEnabled: Boolean get() {
        return _mIsAutoScrollingEnabled
    }

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: GeoChatLayoutManager? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
        mLayoutManager = recyclerView.layoutManager as GeoChatLayoutManager

        mLayoutManager!!.setOnLayoutCompletedCallback {
            if (mIsAutoScrollingEnabled) {
                mRecyclerView!!.smoothScrollToPosition(itemCount)
            }
        }

        mRecyclerView!!.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                val lastVisibleItemPosition = mLayoutManager!!.findLastVisibleItemPosition()

                changeAutoScrollingFlag(lastVisibleItemPosition == itemCount - 1)
            }
        })
    }

    private fun changeAutoScrollingFlag(isEnabled: Boolean) {
        if (mRecyclerView == null || mLayoutManager == null || isEnabled == mIsAutoScrollingEnabled)
            return

        _mIsAutoScrollingEnabled = isEnabled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoChatViewHolder {
        val viewBinding = ComponentChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return GeoChatViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return mMessageAdapterInfoList.size
    }

    override fun onBindViewHolder(holder: GeoChatViewHolder, position: Int) {
        val messageAdapterInfo = mMessageAdapterInfoList[position]

        holder.bind(
            messageAdapterInfo.message,
            mCallback.getUserById(messageAdapterInfo.message.userId))
    }

    fun addMessage(message: Message) {
        mMessageAdapterInfoList.add(MessageAdapterInfo(message))

        notifyItemInserted(itemCount)
    }

    fun setMessages(messages: List<Message>) {
        val prevCount = mMessageAdapterInfoList.size

        mMessageAdapterInfoList.clear()

        notifyItemRangeRemoved(0, prevCount)

        for (message in messages) {
            mMessageAdapterInfoList.add(MessageAdapterInfo(message))
        }

        changeAutoScrollingFlag(true)
        notifyItemRangeInserted(0, itemCount)
    }

    override fun wasViewHolderAnimated(viewHolder: RecyclerView.ViewHolder): Boolean {
        val messageAdapterInfo = mMessageAdapterInfoList[viewHolder.adapterPosition]

        return messageAdapterInfo.wasAnimated
    }

    override fun setViewHolderAnimated(viewHolder: RecyclerView.ViewHolder) {
        val messageAdapterInfo = mMessageAdapterInfoList[viewHolder.adapterPosition]

        messageAdapterInfo.wasAnimated = true
    }
}