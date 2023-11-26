package com.qubacy.geoqq.ui.common.fragment.chat.component.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.databinding.ComponentChatMessageBinding
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.component.animatedlist.adapter.AnimatedListAdapter
import com.qubacy.geoqq.ui.common.util.TimeUtils
import java.util.Locale
import java.util.TimeZone

open class ChatAdapter(
    protected val mCallback: ChatAdapterCallback
) : AnimatedListAdapter<ChatAdapter.GeoChatViewHolder, Message>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoChatViewHolder {
        val viewBinding = ComponentChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return GeoChatViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: GeoChatViewHolder, position: Int) {
        val messageAdapterInfo = mItemAdapterInfoList[position]
        val user = mCallback.getUserById(messageAdapterInfo.item.userId)

        holder.bind(
            messageAdapterInfo.item, user)
        holder.itemView.setOnClickListener {
            mCallback.onMessageClicked(messageAdapterInfo.item)
        }
    }
}