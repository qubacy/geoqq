package com.qubacy.geoqq.ui.screen.geochat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo.UserInfoBottomSheetContentCallback
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapterCallback
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.SetMessagesUiOperation
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch

class GeoChatFragment(

) : LocationFragment(),
    ChatAdapterCallback,
    UserInfoBottomSheetContentCallback
{
    override val mModel: GeoChatViewModel by viewModels {
        GeoChatViewModelFactory()
    }

    private lateinit var mBinding: FragmentGeoChatBinding

    private lateinit var mGeoChatAdapter: ChatAdapter

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

        mBinding.bottomSheet.bottomSheetContentCard.setCallback(this)
        mGeoChatAdapter = ChatAdapter(this)

        mBinding.chatRecyclerView.apply {
            layoutManager = AnimatedListLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mGeoChatAdapter
            itemAnimator = AnimatedListItemAnimator(mGeoChatAdapter)
        }

        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }

        lifecycleScope.launch {
            mModel.geoChatUiOperationFlow.collect {
                if (it == null) return@collect

                onGeoChatUiOperationReceived(it)
            }
        }

        initWithUiState(mModel.geoChatUiState)
    }

    private fun initWithUiState(uiState: ChatUiState) {
        mGeoChatAdapter.setItems(uiState.messages)
    }

    // TODO: REFACTOR THIS!!!

    private fun onGeoChatUiOperationReceived(geoChatUiOperation: UiOperation) {
        when (geoChatUiOperation::class) {
            AddMessageUiOperation::class -> {
                val addMessageOperation = geoChatUiOperation as AddMessageUiOperation

//                mGeoChatAdapter.addItem(addMessageOperation.message)
            }
            SetMessagesUiOperation::class -> {
                val setMessagesOperation = geoChatUiOperation as SetMessagesUiOperation

                mGeoChatAdapter.setItems(setMessagesOperation.messages)
            }
            ShowErrorUiOperation::class -> {
                val showErrorOperation = geoChatUiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorOperation.error)
            }
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

    override fun getUserById(userId: Long): User {
        return mModel.geoChatUiState.users.find {
            it.userId == userId
        }!!
    }

    override fun onMessageClicked(message: Message) {
        val user = getUserById(message.userId)

        mBinding.bottomSheet.bottomSheetContentCard.setData(user)
        mBinding.bottomSheet.bottomSheetContentCard.showPreview()
    }

    override fun addToFriend(user: User) {
        mModel.addToFriend(user)
    }
}