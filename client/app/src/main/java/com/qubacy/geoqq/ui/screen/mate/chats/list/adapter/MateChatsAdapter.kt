package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

        fun bind(chat: MateChat, title: String) {
            mBinding.name.text = title
            mBinding.userAvatar.setImageURI(chat.avatarUri)

            if (chat.lastMessage != null) {
                mBinding.lastMessage.text = chat.lastMessage.text
                mBinding.lastMessageTimestamp.text = TimeUtils.longToHoursMinutesSecondsFormattedString(
                    chat.lastMessage.timestamp, Locale.getDefault(), TimeZone.getDefault()) // todo: is it OK to do it here??
            }
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