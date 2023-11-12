package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.databinding.ComponentMateChatPreviewBinding
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.common.component.animatedlist.adapter.AnimatedListAdapter
import com.qubacy.geoqq.ui.common.util.TimeUtils
import java.util.Locale
import java.util.TimeZone

class MateChatsAdapter(
    private val mCallback: MateChatsAdapterCallback
) : AnimatedListAdapter<MateChatsAdapter.MateChatViewHolder, MateChat>(mIsReversed = true) {
    class MateChatViewHolder(
        private val mBinding: ComponentMateChatPreviewBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(chat: MateChat) {
            mBinding.name.text = chat.chatName

            if (mBinding.lastMessage != null) {
                mBinding.lastMessage.text = chat.lastMessage!!.text
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

        holder.bind(chatAdapterInfo.item)
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
}