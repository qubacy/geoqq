package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import android.graphics.Shader
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui._common.tile.TileDrawable
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.closeSoftKeyboard
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.MateableFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator.MateMessageItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.error.UiMateChatErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerViewCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatFragment(

) : BusinessFragment<FragmentMateChatBinding, MateChatUiState, MateChatViewModel>(),
    PermissionRunnerCallback,
    BaseRecyclerViewCallback,
    UserBottomSheetViewContainerCallback,
    MateableFragment
{
    companion object {
        const val TAG = "MateChatFragment"
    }

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

        mSnackbarAnchorView = mBinding.fragmentMateInputMessageWrapper

        initMessageListView()
        initUiControls()
    }

    override fun onStop() {
        super.onStop()

        mInterlocutorDetailsSheet?.close()
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentMateChatTopBar
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.mateChatFragment
    }

    override fun getFragmentTitle(): String {
        return mModel.uiState.chatContext!!.user.username
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            InsertMessagesUiOperation::class ->
                processInsertMessagesUiOperation(uiOperation as InsertMessagesUiOperation)
            UpdateMessageChunkUiOperation::class ->
                processUpdateChatChunkUiOperation(uiOperation as UpdateMessageChunkUiOperation)
            ShowInterlocutorDetailsUiOperation::class ->
                processShowInterlocutorDetailsUiOperation(
                    uiOperation as ShowInterlocutorDetailsUiOperation
                )
            UpdateInterlocutorDetailsUiOperation::class ->
                processUpdateInterlocutorDetailsUiOperation(
                    uiOperation as UpdateInterlocutorDetailsUiOperation
                )
            MateRequestSentToInterlocutorUiOperation::class ->
                processMateRequestSentToInterlocutorUiOperation(
                    uiOperation as MateRequestSentToInterlocutorUiOperation)
            ChatDeletedUiOperation::class ->
                processChatDeletedUiOperation(uiOperation as ChatDeletedUiOperation)
            MessageSentUiOperation::class ->
                processMessageSentUiOperation(uiOperation as MessageSentUiOperation)
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

    private fun processShowInterlocutorDetailsUiOperation(
        showInterlocutorDetailsUiOperation: ShowInterlocutorDetailsUiOperation
    ) {
        openInterlocutorDetailsSheet(showInterlocutorDetailsUiOperation.interlocutor)
    }

    private fun processUpdateInterlocutorDetailsUiOperation(
        updateInterlocutorDetailsUiOperation: UpdateInterlocutorDetailsUiOperation
    ) {
        adjustUiWithInterlocutor(updateInterlocutorDetailsUiOperation.interlocutor)
    }

    private fun processMateRequestSentToInterlocutorUiOperation(
        mateRequestSentToInterlocutorUiOperation: MateRequestSentToInterlocutorUiOperation
    ) {
        onPopupMessageOccurred(R.string.fragment_mate_chat_snackbar_message_mate_request_sent)
        mInterlocutorDetailsSheet!!.setMateButtonEnabled(false)
    }

    private fun processChatDeletedUiOperation(chatDeletedUiOperation: ChatDeletedUiOperation) {
        Navigation.findNavController(requireView()).navigateUp()
    }

    private fun processMessageSentUiOperation(messageSentUiOperation: MessageSentUiOperation) {
        //onPopupMessageOccurred(R.string.fragment_mate_chat_snackbar_message_message_sent) // not a nice thing actually;
    }

    override fun runInitWithUiState(uiState: MateChatUiState) {
        super.runInitWithUiState(uiState)

        initUiWithUiState(uiState)
    }

    private fun initChatContext() {
        if (mModel.uiState.chatContext == null) mModel.setChatContext(mArgs.chat)
    }

    private fun initUiWithUiState(uiState: MateChatUiState) {
        adjustUiWithInterlocutor(uiState.chatContext!!.user)
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

        setupMessageListBackground()
    }

    private fun setupMessageListBackground() {
        val background = ContextCompat.getDrawable(
            requireContext(), R.drawable.chat_background_pattern)!!
        val tileBackground = TileDrawable(background, Shader.TileMode.REPEAT)

        mBinding.fragmentMateChatList.background = tileBackground
    }

    private fun initUiControls() {
        // todo: implement..

        mBinding.fragmentMateChatTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
        // todo: delete:
        mBinding.fragmentMateInputMessage.setOnKeyListener { _, keyCode, event ->
            onMessageInputKeyPressed(keyCode, event)
        }
    }

    private fun onMessageInputKeyPressed(keyCode: Int, keyEvent: KeyEvent): Boolean {
    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
        launchSendingMessage()

        return true
    }

    return false
}

    private fun setMenuEnabled(isEnabled: Boolean) {
        mBinding.fragmentMateChatTopBar.isEnabled = isEnabled
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        closeSoftKeyboard()
        closePopupMessage()

        when (menuItem.itemId) {
            R.id.mate_chat_top_bar_option_show_mate_profile -> launchShowMateProfile()
            R.id.mate_chat_top_bar_option_delete_chat -> launchDeleteChat()
            else -> return false
        }

        return true
    }

    private fun launchShowMateProfile() {
        val interlocutor = mModel.getInterlocutorProfile()

        openInterlocutorDetailsSheet(interlocutor)
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
//        if (mModel.uiState.prevMessages.isEmpty()) mModel.getNextMessageChunk()
        if (mModel.areMessageChunksInitialized()) resetMessageChunks()

        mModel.getNextMessageChunk()
    }

    private fun resetMessageChunks() {
        mAdapter.resetItems()
        mModel.resetMessageChunks()
    }

    override fun onEndReached() {
        launchPrevMessagesLoading()
    }

    private fun launchPrevMessagesLoading() {
        mModel.getNextMessageChunk()
    }

    private fun adjustUiWithInterlocutor(interlocutor: UserPresentation) {
        setupInterlocutorDetailsSheet(interlocutor)

        mBinding.fragmentMateChatTopBarContentWrapper.title = interlocutor.username

        adjustMessageInputWithInterlocutor(interlocutor)
        adjustTopBarMenuWithInterlocutor(interlocutor)
    }

    private fun adjustMessageInputWithInterlocutor(interlocutor: UserPresentation) {
        val isInterlocutorChatable = mModel.isInterlocutorChatable(interlocutor)

        mBinding.fragmentMateInputMessage.apply {
            isEnabled = isInterlocutorChatable

            if (!isInterlocutorChatable) clearFocus()
        }

        mBinding.fragmentMateInputMessageWrapper
            .setHint(getMessageInputHintByInterlocutor(interlocutor))
    }

    @StringRes
    private fun getMessageInputHintByInterlocutor(interlocutor: UserPresentation): Int {
        return if (interlocutor.isMate && !interlocutor.isDeleted)
            R.string.fragment_mate_chat_input_message_hint_text_chatable
        else if (interlocutor.isDeleted)
            R.string.fragment_mate_chat_input_message_hint_text_deleted
        else
            R.string.fragment_mate_chat_input_message_hint_text_not_mate
    }

    private fun adjustTopBarMenuWithInterlocutor(interlocutor: UserPresentation) {
        val isChatDeletable = mModel.isChatDeletable()

        mBinding.fragmentMateChatTopBar.menu.findItem(
            R.id.mate_chat_top_bar_option_delete_chat).isVisible = isChatDeletable
    }

    override fun onMateButtonClicked() {
        val isMate = mModel.isInterlocutorMate()

        if (isMate) launchDeleteChat()
        else launchAddInterlocutorAsMate()

        mInterlocutorDetailsSheet!!.close()
    }

    private fun launchAddInterlocutorAsMate() {
        if (!mModel.uiState.isMateRequestSendingAllowed) return // todo: is it enough?

        mModel.addInterlocutorAsMate()
    }

    private fun launchDeleteChat() {
        showRequestDialog(
            R.string.fragment_mate_chat_dialog_request_delete_chat_confirmation,
            { mModel.deleteChat() }
        )
    }

    private fun launchSendingMessage() {
        val messageText = mBinding.fragmentMateInputMessage.text.toString()

        if (!mModel.isMessageTextValid(messageText))
            return mModel.retrieveError(UiMateChatErrorType.INVALID_MESSAGE)

        mBinding.fragmentMateInputMessage.text!!.clear()

        mModel.sendMessage(messageText)
    }

    override fun getInterlocutorDetailsSheet(): UserBottomSheetViewContainer? {
        return mInterlocutorDetailsSheet
    }

    override fun isInterlocutorDetailsMateButtonEnabled(interlocutor: UserPresentation): Boolean {
        return mModel.isInterlocutorMateableOrDeletable(interlocutor)
    }

    override fun initInterlocutorDetailsSheet() {
        mInterlocutorDetailsSheet = createInterlocutorDetailsSheet()
    }

    override fun getInterlocutorDetailsSheetExpandedHeight(): Int {
        val topPosition = mBinding.fragmentMateChatTopBarWrapper.bottom
        val bottomPosition = mBinding.root.bottom

        return bottomPosition - topPosition
    }

    override fun getInterlocutorDetailsSheetParent(): CoordinatorLayout {
        return mBinding.root
    }

    override fun getInterlocutorDetailsSheetCallback(): UserBottomSheetViewContainerCallback {
        return this
    }

    override fun getInterlocutorDetailsSheetInsets(): WindowInsetsCompat? {
        return mLastWindowInsets
    }

    override fun adjustUiWithLoadingState(isLoading: Boolean) {
        changeLoadingIndicatorState(isLoading)
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMateChatProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }
}