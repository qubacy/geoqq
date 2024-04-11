package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.view.BaseRecyclerViewCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.closeSoftKeyboard
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator.MateMessageItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatFragment(

) : BusinessFragment<FragmentMateChatBinding, MateChatUiState, MateChatViewModel>(),
    PermissionRunnerCallback,
    BaseRecyclerViewCallback
{
    private val mArgs: MateChatFragmentArgs by navArgs()

    @Inject
    @MateChatViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateChatViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateMessageListAdapter

    private var mInterlocutorDetailsSheet: UserBottomSheetViewContainer? = null
    private var mLastWindowInsets: WindowInsetsCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChatContext() // it's important to run BEFORE super.onStart() & onPermissionGranted();

        runPermissionCheck<MateChatsFragment>()
        setupNavigationUI(mBinding.fragmentMateChatTopBar)

        initMessageListView()
        initUiControls()
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            InsertMessagesUiOperation::class ->
                processInsertMessagesUiOperation(uiOperation as InsertMessagesUiOperation)
            UpdateMessageChunkUiOperation::class ->
                processUpdateChatChunkUiOperation(uiOperation as UpdateMessageChunkUiOperation)
            else -> return false
        }

        return true
    }

    private fun processInsertMessagesUiOperation(
        insertMessagesUiOperation: InsertMessagesUiOperation
    ) {
        val remoteUserId = mModel.uiState.chatContext!!.user.id
        val messageItems = insertMessagesUiOperation.messages
            .map { it.toMateMessageItemData(remoteUserId) }

        mAdapter.insertMateMessages(messageItems, insertMessagesUiOperation.position)
    }

    private fun processUpdateChatChunkUiOperation(
        updateMessageChunkUiOperation: UpdateMessageChunkUiOperation
    ) {
        val remoteUserId = mModel.uiState.chatContext!!.user.id
        val messageItems = updateMessageChunkUiOperation.messages
            .map { it.toMateMessageItemData(remoteUserId) }

        if (updateMessageChunkUiOperation.messageChunkSizeDelta < 0) {
            val itemsToInsertCount = -updateMessageChunkUiOperation.messageChunkSizeDelta
            val itemsToUpdateCount = updateMessageChunkUiOperation.messages.size - itemsToInsertCount

            val itemsToInsert = messageItems.subList(
                itemsToUpdateCount, updateMessageChunkUiOperation.messages.size)
            val itemsToUpdate = messageItems.subList(0, itemsToUpdateCount)

            mAdapter.insertMateMessages(itemsToInsert, itemsToUpdateCount)
            mAdapter.updateMateMessageChunk(itemsToUpdate, 0)

        } else {
            mAdapter.updateMateMessageChunk(messageItems, updateMessageChunkUiOperation.position)

            if (updateMessageChunkUiOperation.messageChunkSizeDelta > 0) {
                mAdapter.deleteMateMessages(
                    updateMessageChunkUiOperation.position +
                            updateMessageChunkUiOperation.messages.size,
                    updateMessageChunkUiOperation.messageChunkSizeDelta
                )
            }
        }
    }

    override fun runInitWithUiState(uiState: MateChatUiState) {
        super.runInitWithUiState(uiState)

        initUiWithUiState(uiState)
    }

    private fun initChatContext() {
        if (mModel.uiState.chatContext == null) mModel.setChatContext(mArgs.chat)
    }

    private fun initUiWithUiState(uiState: MateChatUiState) {
        mBinding.fragmentMateChatTopBar.title = uiState.chatContext!!.user.username
    }

    private fun initMessageListView() {
        mAdapter = MateMessageListAdapter()

        mBinding.fragmentMateChatList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, true)
            adapter = mAdapter
            itemAnimator = MateMessageItemAnimator()

            setCallback(this@MateChatFragment)
        }
    }

    private fun initUiControls() {
        // todo: implement..

        mBinding.fragmentMateChatTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
    }

    private fun setMenuEnabled(isEnabled: Boolean) {
        mBinding.fragmentMateChatTopBar.isEnabled = isEnabled
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        closeSoftKeyboard()

        when (menuItem.itemId) {
            R.id.mate_chat_top_bar_option_delete_mate -> launchDeleteMate()
            else -> return false
        }

        return true
    }

    private fun launchDeleteMate() {
        // todo: implement..

        // todo: delete:
        openInterlocutorDetailsSheet()
    }

    override fun viewInsetsToCatch(): Int {
        return super.viewInsetsToCatch() or WindowInsetsCompat.Type.ime()
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mLastWindowInsets = insetsRes

        mBinding.fragmentMateChatTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentMateInputMessageWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
        mBinding.fragmentMateChatList.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                this@updateLayoutParams.bottomMargin =
                    mBinding.fragmentMateInputMessageWrapper.measuredHeight
            }
        }

        mInterlocutorDetailsSheet?.apply {
            adjustToInsets(insetsRes)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateChatBinding {
        return FragmentMateChatBinding.inflate(inflater)
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        initMateChat()
    }

    private fun initMateChat() {
        if (mModel.uiState.messageChunks.isEmpty()) mModel.getNextMessageChunk()
    }

    override fun onEndReached() {
        launchPrevMessagesLoading()
    }

    private fun launchPrevMessagesLoading() {
        mModel.getNextMessageChunk()
    }

    private fun openInterlocutorDetailsSheet() {
        if (mInterlocutorDetailsSheet == null) initInterlocutorDetailsSheet()

        mInterlocutorDetailsSheet!!.setUserData(mModel.uiState.chatContext!!.user)
        mInterlocutorDetailsSheet!!.open()
    }

    private fun initInterlocutorDetailsSheet() {
        val expandedBottomSheetHeight = getExpandedBottomSheetHeight()
        val collapsedBottomSheetHeight = expandedBottomSheetHeight / 2

        mInterlocutorDetailsSheet = UserBottomSheetViewContainer(
            requireContext(),
            mBinding.root,
            expandedBottomSheetHeight,
            collapsedBottomSheetHeight
        ).apply {
            adjustToInsets(mLastWindowInsets!!)
        }

        mBinding.root.addView(mInterlocutorDetailsSheet!!.getView())
    }

    private fun getExpandedBottomSheetHeight(): Int {
        val topPosition = mBinding.fragmentMateChatTopBarWrapper.bottom
        val bottomPosition = mBinding.root.bottom

        return bottomPosition - topPosition
    }
}