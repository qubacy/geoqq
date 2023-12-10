package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentMateChatPreviewBinding
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.common.visual.component.animatedlist.adapter.AnimatedListAdapter
import com.qubacy.geoqq.ui.common.util.TimeUtils
import java.util.Locale
import java.util.TimeZone

class MateChatsAdapter(
    private val mCallback: MateChatsAdapterCallback
) : AnimatedListAdapter<MateChatsAdapter.MateChatViewHolder, MateChat>(
    true, mCallback
) {
    class MateChatViewHolder(
        private val mBinding: ComponentMateChatPreviewBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        companion object {
            const val NEW_MESSAGE_COUNT_MAX = 99
        }

        private fun setNewMessageCount(newMessageCount: Int) {
            mBinding.newMessageCount.newMessageCountLabelValue.text =
                if (newMessageCount < NEW_MESSAGE_COUNT_MAX) newMessageCount.toString()
                else NEW_MESSAGE_COUNT_MAX.toString()
            mBinding.newMessageCount.root.visibility =
                if (newMessageCount <= 0) View.GONE else View.VISIBLE
        }

        fun bind(chat: MateChat, title: String) {
            mBinding.name.text = title
            mBinding.userAvatar.setImageURI(chat.avatarUri)

            setNewMessageCount(chat.newMessageCount)

            if (chat.lastMessage != null) {
                mBinding.lastMessage.text = chat.lastMessage.text
                mBinding.lastMessageTimestamp.text = TimeUtils.longToHoursMinutesSecondsFormattedString(
                    chat.lastMessage.timestamp, Locale.getDefault(), TimeZone.getDefault()) // todo: is it OK to do it here??
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateChatViewHolder {
        val viewBinding = DataBindingUtil.inflate<ComponentMateChatPreviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.component_mate_chat_preview,
            parent,
            false
        )

        return MateChatViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MateChatViewHolder, position: Int) {
        val chatAdapterInfo = mItemAdapterInfoList[position]

        val interlocutorUser = mCallback.getUser(chatAdapterInfo.item.interlocutorUserId)

        holder.bind(chatAdapterInfo.item, interlocutorUser.username)
        holder.itemView.setOnClickListener {
            mCallback.onChatClicked(chatAdapterInfo.item, holder.itemView)
        }
    }

    override fun changeItem(item: MateChat): Int {
        val changedItemPos = super.changeItem(item)
        val updatedItemInfo = mItemAdapterInfoList[changedItemPos]

        mItemAdapterInfoList.remove(updatedItemInfo)
        mItemAdapterInfoList.add(0, updatedItemInfo)

        notifyItemMoved(changedItemPos, 0)

        return 0
    }

    fun updateChatUsersData(usersIds: List<Long>) {
        for (userId in usersIds) {
            val itemInfo = mItemAdapterInfoList.find { it.item.interlocutorUserId == userId } ?: return

            val pos = super.changeItem(itemInfo.item)

            notifyItemChanged(pos)
        }
    }
}