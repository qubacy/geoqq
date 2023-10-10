package com.qubacy.geoqq.ui.screen.geochat.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.databinding.ComponentChatMessageBinding
import com.qubacy.geoqq.ui.common.util.TimeUtils
import com.qubacy.geoqq.ui.screen.geochat.chat.animator.ChatMessageAnimatorCallback
import java.util.Locale
import java.util.TimeZone

class GeoChatAdapter(
    private val mCallback: GeoChatAdapterCallback
) : ListAdapter<Message, GeoChatAdapter.GeoChatViewHolder>(DiffCallback), ChatMessageAnimatorCallback {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return (oldItem == newItem)
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return (oldItem.timestamp == newItem.timestamp
                    && oldItem.text == newItem.text)
            }
        }
    }

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

    private val mShownMessageHashList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoChatViewHolder {
        val viewBinding = ComponentChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return GeoChatViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: GeoChatViewHolder, position: Int) {
        val message = getItem(position)

        holder.bind(message, mCallback.getUserById(message.userId))
    }

    override fun wasViewHolderAnimated(viewHolder: RecyclerView.ViewHolder): Boolean {
        Log.d("TEST", "wasViewHolderAnimated(): adapterPos = ${viewHolder.adapterPosition}")

        val message = getItem(viewHolder.adapterPosition)

        return (mShownMessageHashList.find { it == message.hashCode() } != null)
    }

    override fun setViewHolderAnimated(viewHolder: RecyclerView.ViewHolder) {
        val message = getItem(viewHolder.adapterPosition)

        mShownMessageHashList.add(message.hashCode())
    }
}