package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.databinding.ComponentMateChatPreviewBinding
import com.qubacy.geoqq.ui.common.component.animatedlist.adapter.AnimatedListAdapter
import com.qubacy.geoqq.ui.common.util.TimeUtils
import java.util.Locale
import java.util.TimeZone

class MateChatsAdapter(
    private val mCallback: MateChatsAdapterCallback
) : AnimatedListAdapter<MateChatsAdapter.MateChatViewHolder, Chat>() {
    class MateChatViewHolder(
        private val mBinding: ComponentMateChatPreviewBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(chat: Chat) {
            mBinding.lastMessage.text = chat.lastMessage.text
            mBinding.name.text = chat.chatName
            mBinding.lastMessageTimestamp.text = TimeUtils.longToHoursMinutesSecondsFormattedString(
                chat.lastMessage.timestamp, Locale.getDefault(), TimeZone.getDefault()) // todo: is it OK to do it here??
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateChatViewHolder {
        val viewBinding = ComponentMateChatPreviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MateChatViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MateChatViewHolder, position: Int) {
        val chatAdapterInfo = mItemAdapterInfoList[position]

        holder.bind(chatAdapterInfo.item)
        holder.itemView.setOnClickListener {
            mCallback.onChatClicked(chatAdapterInfo.item)
        }
    }
}