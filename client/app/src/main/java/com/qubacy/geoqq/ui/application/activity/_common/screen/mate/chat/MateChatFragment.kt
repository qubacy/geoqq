package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import android.graphics.Shader
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui._common.tile.TileDrawable
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizedFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.operation.handler.AuthorizedUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.ChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.extension.closeSoftKeyboard
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.animator.MessageItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.error.type.UiChatErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.operation.handler.ChatUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.operation.handler.InterlocutorUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.state.MateChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.operation.handler.MateChatUiOperationHandler
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerViewCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatFragment(

) : BusinessFragment<FragmentMateChatBinding, MateChatUiState, MateChatViewModel>(),
    PermissionRunnerCallback,
    BaseRecyclerViewCallback,
    UserBottomSheetViewContainerCallback,
    InterlocutorFragment,
    AuthorizedFragment,
    ChatFragment,
    PopupFragment
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

    override var messageSnackbar: Snackbar? = null

    override fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return super.generateUiOperationHandlers()
            .plus(MateChatUiOperationHandler(this))
            .plus(ChatUiOperationHandler(this))
            .plus(InterlocutorUiOperationHandler(this))
            .plus(AuthorizedUiOperationHandler(this))
    }

    override fun getPopupAnchorView(): View {
        return mBinding.fragmentMateChatInputMessageWrapper
    }

    override fun getPopupFragmentBaseFragment(): BaseFragment<*> {
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChatContext() // it's important to run BEFORE super.onStart() & onPermissionGranted();
        initMessageListView()

        runPermissionCheck<MateChatFragment>()

        initUiControls()
    }

    override fun onStop() {
        super.onStop()

        closePopupMessage()
        closeInterlocutorDetailsSheet()
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

    fun onMateChatFragmentInsertMessages(
        messages: List<MateMessagePresentation>,
        position: Int
    ) {
        val remoteUserId = mModel.uiState.chatContext!!.user.id
        val messageItems = messages
            .map { it.toMateMessageItemData(remoteUserId) }

        mAdapter.insertMateMessages(messageItems, position)
    }

    fun onMateChatFragmentUpdateMessages(
        messages: List<MateMessagePresentation>,
        position: Int,
        messageChunkSizeDelta: Int
    ) {
        val remoteUserId = mModel.uiState.chatContext!!.user.id
        val messageItems = messages
            .map { it.toMateMessageItemData(remoteUserId) }

        if (messageChunkSizeDelta < 0) {
            val itemsToInsertCount = -messageChunkSizeDelta
            val itemsToUpdateCount = messages.size - itemsToInsertCount

            val itemsToInsert = messageItems.subList(
                itemsToUpdateCount, messages.size)
            val itemsToUpdate = messageItems.subList(0, itemsToUpdateCount)

            mAdapter.insertMateMessages(itemsToInsert, itemsToUpdateCount)
            mAdapter.updateMateMessageChunk(itemsToUpdate, 0)

        } else {
            mAdapter.updateMateMessageChunk(messageItems, position)

            if (messageChunkSizeDelta > 0) {
                mAdapter.deleteMateMessages(position + messages.size, messageChunkSizeDelta)
            }
        }
    }

    fun onMateChatFragmentChatContextUpdated(chatContext: MateChatPresentation) {
        adjustMessageInputWithChatContext(chatContext)
        adjustTopBarMenuWithChatContext(chatContext)
    }

    override fun onChatFragmentMateRequestSent() {
        super.onChatFragmentMateRequestSent()

        mInterlocutorDetailsSheet!!.setMateButtonEnabled(false)
    }

    fun onMateChatFragmentChatDeleted() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun runInitWithUiState(uiState: MateChatUiState) {
        super.runInitWithUiState(uiState)

        initUiWithUiState(uiState)
    }

    private fun initChatContext() {
        if (mModel.uiState.chatContext == null) mModel.setChatContext(mArgs.chat)
    }

    private fun initUiWithUiState(uiState: MateChatUiState) {
        adjustInterlocutorFragmentUiWithInterlocutor(uiState.chatContext!!.user)
    }

    private fun initMessageListView() {
        mAdapter = MateMessageListAdapter()

        mBinding.fragmentMateChatList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, true)
            adapter = mAdapter
            itemAnimator = MessageItemAnimator()

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
        mBinding.fragmentMateChatTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }

        val chatContext = mModel.uiState.chatContext!!

        adjustMessageInputWithChatContext(chatContext)

        mBinding.fragmentMateChatInputMessage.setOnKeyListener { _, keyCode, event ->
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
        mBinding.fragmentMateChatInputMessageWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
        mBinding.fragmentMateChatList.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                this@updateLayoutParams.bottomMargin =
                    mBinding.fragmentMateChatInputMessageWrapper.measuredHeight
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

    override fun onRequestedPermissionsGranted() {
        initMateChat()
    }

    private fun initMateChat() {
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

    override fun adjustInterlocutorFragmentUiWithInterlocutor(interlocutor: UserPresentation) {
        super.adjustInterlocutorFragmentUiWithInterlocutor(interlocutor)

        mBinding.fragmentMateChatTopBarContentWrapper.title = interlocutor.username
    }

    private fun adjustMessageInputWithChatContext(chatContext: MateChatPresentation) {
        setMessageInputEnabledWithChatContext(chatContext)

        mBinding.fragmentMateChatInputMessageWrapper
            .setHint(getMessageInputHintByChatContext(chatContext))
    }

    private fun setMessageInputEnabledWithChatContext(chatContext: MateChatPresentation) {
        val isInterlocutorChatable = mModel.isInterlocutorChatable(chatContext.user)

        setMessageInputEnabled(isInterlocutorChatable)
    }

    private fun setMessageInputEnabled(enable: Boolean) {
        Log.d(TAG, "setMessageInputEnabled(): enable = $enable;")

        mBinding.fragmentMateChatInputMessage.apply {
            isEnabled = enable

            if (!enable) clearFocus()
        }
    }

    @StringRes
    private fun getMessageInputHintByChatContext(chatContext: MateChatPresentation): Int {
        val interlocutor = chatContext.user

        return if (mModel.isInterlocutorChatable(interlocutor))
            R.string.fragment_mate_chat_input_message_hint_text_chatable
        else if (interlocutor.isDeleted)
            R.string.fragment_mate_chat_input_message_hint_text_deleted
        else
            R.string.fragment_mate_chat_input_message_hint_text_not_mate
    }

    private fun adjustTopBarMenuWithChatContext(chatContext: MateChatPresentation) {
        val isChatDeletable = mModel.isChatDeletable(chatContext.user)

        mBinding.fragmentMateChatTopBar.menu.findItem(
            R.id.mate_chat_top_bar_option_delete_chat).isVisible = isChatDeletable
    }

    override fun onMateButtonClicked(userId: Long) {
        val isMate = mModel.isInterlocutorMate()

        if (isMate) launchDeleteChat()
        else launchAddInterlocutorAsMate()

        closeInterlocutorDetailsSheet()
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
        val messageText = mBinding.fragmentMateChatInputMessage.text.toString().trim()

        if (!mModel.isMessageTextValid(messageText))
            return mModel.retrieveError(UiChatErrorType.INVALID_MESSAGE)

        mBinding.fragmentMateChatInputMessage.text!!.clear()

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
        setMessageInputEnabled(!isLoading && mModel.isInterlocutorChatable())
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMateChatProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToLogin() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mateChatFragment_to_loginFragment)
    }

    override fun getPopupFragmentForChatFragment(): PopupFragment {
        return this
    }
}