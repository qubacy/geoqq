package com.qubacy.geoqq.ui.screen.geochat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.geochat.chat.adapter.GeoChatAdapter
import com.qubacy.geoqq.ui.screen.geochat.chat.adapter.GeoChatAdapterCallback
import com.qubacy.geoqq.ui.screen.geochat.chat.animator.ChatMessageAnimator
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatUiState
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory
import com.yandex.mapkit.geometry.Point

class GeoChatFragment() : LocationFragment(), GeoChatAdapterCallback {
    override val mModel: GeoChatViewModel by viewModels {
        GeoChatViewModelFactory()
    }

    private lateinit var mBinding: FragmentGeoChatBinding

    private lateinit var mGeoChatAdapter: GeoChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_geo_chat,
            container,
            false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGeoChatAdapter = GeoChatAdapter(this)

        mBinding.chatRecyclerView.apply {
            adapter = mGeoChatAdapter
            itemAnimator = ChatMessageAnimator(mGeoChatAdapter)
        }

        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }

        mModel.geoChatUiState.observe(viewLifecycleOwner) {
            onGeoChatUiStateChanged(it)
        }
    }

    private fun onGeoChatUiStateChanged(geoChatUiState: GeoChatUiState) {
        mGeoChatAdapter.submitList(geoChatUiState.messageList) {
            mBinding.chatRecyclerView.scrollToPosition(geoChatUiState.messageList.size - 1)
        }
    }

    private fun onSendingMessageButtonClicked() {
        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        if (!mModel.isMessageCorrect(messageText)) {
            showMessage(R.string.error_chat_message_incorrect, 400)

            return
        }

        mBinding.messageSendingSection.sendingMessage.text?.clear()

        mModel.sendMessage(messageText)
    }

    override fun onLocationPointChanged(newLocationPoint: Point) {
        // todo: mb it'd be nice to use this somehow in the UI??


    }

    override fun handleError(error: Error) {
        // todo: handling the error..


    }

    override fun getUserById(userId: Long): User {
        return mModel.geoChatUiState.value!!.userList.find {
            it.userId == userId
        }!!
    }
}