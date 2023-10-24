package com.qubacy.geoqq.ui.screen.mate.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.mates.chat.entity.MateChat
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.common.fragment.common.base.BaseFragment
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapterCallback
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddUserUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeChatInfoUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory

class MateChatFragment() : BaseFragment(), ChatAdapterCallback, MenuProvider {
    private val mArgs by navArgs<MateChatFragmentArgs>()
    override val mModel: MateChatViewModel by viewModels {
        MateChatViewModelFactory(mArgs.chatId)
    }

    private lateinit var mBinding: FragmentMateChatBinding
    private lateinit var mAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }

        requireActivity().addMenuProvider(this)
    }

    override fun onDestroy() {
        requireActivity().removeMenuProvider(this)

        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_mate_chat,
            container,
            false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(mBinding.chatActionBar)

        mAdapter = ChatAdapter(this)

        mBinding.chatRecyclerView.apply {
            layoutManager = AnimatedListLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
            itemAnimator = AnimatedListItemAnimator(mAdapter)
        }
        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }

        mModel.mateChatUiStateFlow.value?.let {
            initChat(it)
        }
        mModel.mateChatUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun onChatUiStateGotten(chatUiState: ChatUiState) {
        val isListEmpty = mAdapter.itemCount <= 0

        if (isListEmpty) initChat(chatUiState)
        if (chatUiState.newUiOperations.isEmpty()) return

        for (uiOperation in chatUiState.newUiOperations) {
            processUiOperation(uiOperation, isListEmpty)
        }
    }

    private fun initChat(chatUiState: ChatUiState) {
        setChatInfo(chatUiState.chat as MateChat)

        mAdapter.setItems(chatUiState.messages)

        // what else?
    }

    private fun processUiOperation(uiOperation: UiOperation, isListEmpty: Boolean) {
        when (uiOperation::class) {
            AddMessageUiOperation::class -> {
                if (isListEmpty) return

                val addMessageUiOperation = uiOperation as AddMessageUiOperation
                val message = mModel.mateChatUiStateFlow.value!!.messages.find {
                    it.messageId == addMessageUiOperation.messageId
                }!!

                mAdapter.addItem(message)
            }
            AddUserUiOperation::class -> {
                val addUserUiOperation = uiOperation as AddUserUiOperation

                // todo: mb some stuff to visualize a new user's entrance..


            }
            ChangeChatInfoUiOperation::class -> {
                val changeChatInfoUiOperation = uiOperation as ChangeChatInfoUiOperation

                setChatInfo(mModel.mateChatUiStateFlow.value!!.chat as MateChat)
            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun setChatInfo(mateChat: MateChat) {
        mBinding.chatTitle.text = mateChat.chatName

        // what else??
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

    override fun getUserById(userId: Long): User {
        return mModel.mateChatUiStateFlow.value!!.users.find {
            it.userId == userId
        }!!
    }

    override fun onMessageClicked(message: Message) {
        // nothing??
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.mate_chat_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.mate_chat_menu_show_user_info_action -> {
                onShowUserInfoActionClicked()

                return true
            }
        }

        return false
    }

    private fun onShowUserInfoActionClicked() {
        closeSoftKeyboard()

        val user = mModel.getMateInfo()

        mBinding.bottomSheet.bottomSheetContentCard.setData(user)
        mBinding.bottomSheet.bottomSheetContentCard.showPreview()
    }
}