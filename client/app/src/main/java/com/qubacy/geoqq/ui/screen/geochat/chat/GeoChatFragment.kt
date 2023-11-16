package com.qubacy.geoqq.ui.screen.geochat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import com.google.android.material.transition.MaterialFade
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.Application
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo.UserInfoBottomSheetContentCallback
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapterCallback
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.geochat.chat.model.operation.AddUserUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeChatInfoUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.yandex.mapkit.geometry.Point

class GeoChatFragment(

) : LocationFragment(),
    ChatAdapterCallback,
    UserInfoBottomSheetContentCallback
{
    private lateinit var mBinding: FragmentGeoChatBinding

    private lateinit var mGeoChatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: decide what to do with this abrupt animation:
//        enterTransition = Fade().apply {
//            mode = MaterialFade.MODE_IN
//            interpolator = AccelerateDecelerateInterpolator()
//            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
//        }
        returnTransition = Fade().apply {
            mode = MaterialFade.MODE_OUT
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }

        mModel = GeoChatViewModelFactory().create(GeoChatViewModel::class.java)
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

        (mModel as GeoChatViewModel).geoChatUiStateFlow.value?.let {
            initChat(it)
        }
            (mModel as GeoChatViewModel).geoChatUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatUiStateGotten(it)
        }
    }

    private fun initChat(chatUiState: ChatUiState) {
        mGeoChatAdapter.setItems(chatUiState.messages)
    }

    private fun onChatUiStateGotten(chatUiState: ChatUiState) {
        val isListEmpty = mGeoChatAdapter.itemCount <= 0

        if (isListEmpty) initChat(chatUiState)
        if (chatUiState.uiOperationCount() <= 0) return

        while (true) {
            val uiOperation = chatUiState.takeUiOperation() ?: break

            processUiOperation(uiOperation, isListEmpty)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation, isListEmpty: Boolean) {
        when (uiOperation::class) {
            AddMessageUiOperation::class -> {
                if (isListEmpty) return

                val addMessageUiOperation = uiOperation as AddMessageUiOperation
                val message = (mModel as GeoChatViewModel).geoChatUiStateFlow.value!!.messages.find {
                    it.id == addMessageUiOperation.messageId
                }!!

                mGeoChatAdapter.addItem(message)
            }
            AddUserUiOperation::class -> {
                val addUserUiOperation = uiOperation as AddUserUiOperation

                // todo: mb some stuff to visualize a new user's entrance..


            }
            ChangeChatInfoUiOperation::class -> {
                val changeChatInfoUiOperation = uiOperation as ChangeChatInfoUiOperation

                // todo: think of a possible application..


            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun onSendingMessageButtonClicked() {
        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        if (!(mModel as GeoChatViewModel).isMessageCorrect(messageText)) {
            showMessage(R.string.error_chat_message_incorrect, 400)

            return
        }

        mBinding.messageSendingSection.sendingMessage.text?.clear()

                (mModel as GeoChatViewModel).sendMessage(messageText)
    }

    override fun onLocationPointChanged(newLocationPoint: Point) {
        // todo: mb it'd be nice to use this somehow in the UI??


    }

    override fun initFlowContainerIfNull() {
        // todo: implement..


    }

    override fun clearFlowContainer() {
        // todo: implement:

        //(requireActivity().application as Application).appContainer.clearGeoChatContainer()
    }

    override fun getUserById(userId: Long): User {
        return (mModel as GeoChatViewModel).geoChatUiStateFlow.value!!.users.find {
            it.id== userId
        }!!
    }

    override fun onMessageClicked(message: Message) {
        if ((mModel as GeoChatViewModel).isLocalUser(message.userId)) return

        closeSoftKeyboard()

        val user = getUserById(message.userId)

        mBinding.bottomSheet.bottomSheetContentCard.setData(user)
        mBinding.bottomSheet.bottomSheetContentCard.showPreview()
    }

    override fun addToFriend(user: User) {
        (mModel as GeoChatViewModel).addToFriend(user)
    }
}