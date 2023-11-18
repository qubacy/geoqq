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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.Application
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapterCallback
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeChatInfoUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeUsersUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.state.MateChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.SetMessagesUiOperation

class MateChatFragment() : WaitingFragment(), ChatAdapterCallback, MenuProvider {
    private val mArgs by navArgs<MateChatFragmentArgs>()

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

    override fun initFlowContainerIfNull() {
        val application = (requireActivity().application as Application)

        if (application.appContainer.mateChatContainer != null) return

        application.appContainer.initMateChatContainer(
            mArgs.chatId,
            mArgs.interlocutorUserId,
            application.appContainer.errorDataRepository,
            application.appContainer.tokenDataRepository,
            application.appContainer.mateMessageDataRepository,
            application.appContainer.imageDataRepository,
            application.appContainer.userDataRepository
        )

        mModel = application.appContainer.mateChatContainer!!
            .mateChatViewModelFactory
            .create(MateChatViewModel::class.java)
    }

    override fun clearFlowContainer() {
        (requireActivity().application as Application).appContainer.clearMateChatContainer()
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
            //itemAnimator = AnimatedListItemAnimator(mAdapter) // todo: has to be fixed in advance!!
        }
        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }

        (mModel as MateChatViewModel).mateChatUiStateFlow.value?.let {
            initChat(it)
        }
            (mModel as MateChatViewModel).mateChatUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
            (mModel as MateChatViewModel).getMessages()
        }
    }

    private fun onChatUiStateGotten(chatUiState: MateChatUiState) {
        if (chatUiState.uiOperationCount() <= 0) return

        while (true) {
            val uiOperation = chatUiState.takeUiOperation() ?: break

            processUiOperation(chatUiState, uiOperation)
        }
    }

    private fun initChat(chatUiState: MateChatUiState) {
        setChatInfo(chatUiState.title)

        mBinding.chatRecyclerView.itemAnimator = null
        mAdapter.setItems(chatUiState.messages)

        // todo: is it ok?:
        mBinding.chatRecyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            mBinding.chatRecyclerView.itemAnimator = AnimatedListItemAnimator(mAdapter)
        }

        // what else?
    }

    private fun processUiOperation(
        chatUiState: MateChatUiState,
        uiOperation: UiOperation
    ) {
        when (uiOperation::class) {
            SetMessagesUiOperation::class -> {
                initChat(chatUiState)
            }
            AddMessageUiOperation::class -> {
                val addMessageUiOperation = uiOperation as AddMessageUiOperation
                val message = chatUiState.messages.find { it.id == addMessageUiOperation.messageId }!!

                mAdapter.addItem(message)
            }
            OpenUserDetailsUiOperation::class -> {
                val openUserDetailsUiOperation = uiOperation as OpenUserDetailsUiOperation
                val user = chatUiState.users.find { it.id == openUserDetailsUiOperation.userId }!!

                processOpenUserDetailsOperation(openUserDetailsUiOperation, user)
            }
            ChangeUsersUiOperation::class -> {
                val changeUserUiOperation = uiOperation as ChangeUsersUiOperation

                processChangeUserOperation(changeUserUiOperation, chatUiState)
            }
            ChangeChatInfoUiOperation::class -> {
                val changeChatInfoUiOperation = uiOperation as ChangeChatInfoUiOperation

                setChatInfo(chatUiState.title)
            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun processChangeUserOperation(
        changeUsersUiOperation: ChangeUsersUiOperation,
        chatUiState: MateChatUiState
    ) {
        val foundInterlocutorUserId = changeUsersUiOperation.usersIds
            .find { it == (mModel as MateChatViewModel).interlocutorUserId }

        if (foundInterlocutorUserId != null)
            setChatInfo((mModel as MateChatViewModel).mateChatUiStateFlow.value!!.title)

        initChat(chatUiState)
    }

    private fun processOpenUserDetailsOperation(
        openUserDetailsUiOperation: OpenUserDetailsUiOperation,
        user: User
    ) {
        closeSoftKeyboard()

        mBinding.bottomSheet.bottomSheetContentCard.setData(user)
        mBinding.bottomSheet.bottomSheetContentCard.showPreview()
    }

    private fun setChatInfo(title: String) {
        mBinding.chatTitle.text = title

        // what else??
    }

    private fun onSendingMessageButtonClicked() {
        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        if (!(mModel as MateChatViewModel).isMessageCorrect(messageText)) {
            showMessage(R.string.error_chat_message_incorrect, 400)

            return
        }

        mBinding.messageSendingSection.sendingMessage.text?.clear()

        (mModel as MateChatViewModel).sendMessage(messageText)
    }

    override fun getUserById(userId: Long): User {
        val user = (mModel as MateChatViewModel).mateChatUiStateFlow.value!!.users
            .find { it.id == userId }!!

        return user
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
        (mModel as MateChatViewModel).getMateUserDetails()
    }
}