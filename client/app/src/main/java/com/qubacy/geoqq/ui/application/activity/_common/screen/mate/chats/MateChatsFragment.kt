package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui._common.util.view.extension.runVisibilityAnimation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerViewCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatsFragment(

) : BusinessFragment<
    FragmentMateChatsBinding,
    MateChatsUiState,
    MateChatsViewModel
>(), PermissionRunnerCallback, BaseRecyclerViewCallback, MateChatsListAdapterCallback {
    companion object {
        const val DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION = 200L
    }

    @Inject
    @MateChatsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateChatsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateChatsListAdapter
    private lateinit var mPermissionRunner: PermissionRunner<MateChatsFragment>

    private lateinit var mEmptyListHolderImage: AnimatedVectorDrawableCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(owner = this) { }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigationUI(mBinding.fragmentMateChatsTopBar)
        initMateChatListView()
        requestChatPermissions()

        mEmptyListHolderImage = AnimatedVectorDrawableCompat.create(
            requireContext(), R.drawable.ic_earth_animated)!!

        mBinding.fragmentMateChatsTopBar.setOnMenuItemClickListener {
            onTopBarMenuItemClicked(it)
        }

        initEmptyListPlaceholderView()
    }

    private fun initEmptyListPlaceholderView() {
        mBinding.fragmentMateChatsPlaceholderImage.setImageDrawable(mEmptyListHolderImage)

        mBinding.root.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                mEmptyListHolderImage.start()

                return true
            }
        })
    }

    private fun checkMateChatListEmpty() {
        setEmptyListPlaceholderVisible(mAdapter.itemCount <= 0) // todo: mb it'd be better to optimize this;
    }

    private fun setEmptyListPlaceholderVisible(isVisible: Boolean) {
        if (isVisible) {
            mBinding.fragmentMateChatsPlaceholderContainer.runVisibilityAnimation(
                true, DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION)

            mEmptyListHolderImage.start()

        } else {
            mBinding.fragmentMateChatsPlaceholderContainer.runVisibilityAnimation(
                false, DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION)
        }
    }

    private fun requestChatPermissions() {
        mPermissionRunner = PermissionRunner(this).apply {
            requestPermissions()
        }
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions) initMateChats()
    }

    private fun onTopBarMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.main_top_bar_option_my_profile -> onMyProfileMenuItemClicked()
            else -> return false
        }

        return true
    }

    private fun onMyProfileMenuItemClicked() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mateChatsFragment_to_myProfileFragment)
    }

    override fun runInitWithUiState(uiState: MateChatsUiState) {
        super.runInitWithUiState(uiState)

        if (uiState.chats.isNotEmpty() && mAdapter.itemCount <= 0)
            initMateChatListAdapterWithChats(uiState.chats)
        if (uiState.isLoading) changeLoadingIndicatorState(true)
    }

    private fun initMateChatListAdapterWithChats(chats: List<MateChatPresentation>) {
        val chatsItemData = chats.map { it.toMateChatItemData() }

        mAdapter.setMateChats(chatsItemData)
    }

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        initMateChats()
    }

    private fun initMateChats() {
        if (mModel.uiState.chats.isNotEmpty()) resetChatChunks() // todo: is it ok?

        mModel.getNextChatChunk()
    }

    private fun resetChatChunks() {
        mModel.resetChatChunks()
        mAdapter.resetItems()
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            InsertChatsUiOperation::class ->
                processInsertChatsUiOperation(uiOperation as InsertChatsUiOperation)
            UpdateChatChunkUiOperation::class ->
                processUpdateChatChunkUiOperation(uiOperation as UpdateChatChunkUiOperation)
            else -> return false
        }

        return true
    }

    private fun processInsertChatsUiOperation(insertChatsUiOperation: InsertChatsUiOperation) {
        val chatItems = insertChatsUiOperation.chats.map { it.toMateChatItemData() }

        mAdapter.insertMateChats(chatItems, insertChatsUiOperation.position)

        checkMateChatListEmpty()
    }

    private fun processUpdateChatChunkUiOperation(
        updateChatChunkUiOperation: UpdateChatChunkUiOperation
    ) {
        val chatItems = updateChatChunkUiOperation.chats.map { it.toMateChatItemData() }

        if (updateChatChunkUiOperation.chatChunkSizeDelta < 0) {
            val itemsToInsertCount = -updateChatChunkUiOperation.chatChunkSizeDelta
            val itemsToUpdateCount = updateChatChunkUiOperation.chats.size - itemsToInsertCount

            val itemsToInsert = chatItems.subList(
                itemsToUpdateCount, updateChatChunkUiOperation.chats.size)
            val itemsToUpdate = chatItems.subList(0, itemsToUpdateCount)

            mAdapter.insertMateChats(itemsToInsert, itemsToUpdateCount)
            mAdapter.updateMateChatsChunk(itemsToUpdate, 0)

        } else {
            mAdapter.updateMateChatsChunk(chatItems, updateChatChunkUiOperation.position)

            if (updateChatChunkUiOperation.chatChunkSizeDelta > 0) {
                mAdapter.deleteMateChats(
                    updateChatChunkUiOperation.position + updateChatChunkUiOperation.chats.size,
                    updateChatChunkUiOperation.chatChunkSizeDelta
                )
            }
        }

        checkMateChatListEmpty()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateChatsBinding {
        return FragmentMateChatsBinding.inflate(inflater, container, false)
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mBinding.fragmentMateChatsTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentMateChatsList.apply {
            updatePadding(bottom = insets.bottom)
        }
    }

    override fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {
        super.processSetLoadingOperation(loadingOperation)

        changeLoadingIndicatorState(loadingOperation.isLoading)
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMateChatsProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun initMateChatListView() {
        mAdapter = MateChatsListAdapter(callback = this)

        val itemDivider = MaterialDividerItemDecoration(
            requireContext(), MaterialDividerItemDecoration.VERTICAL)

        mBinding.fragmentMateChatsList.apply {
            addItemDecoration(itemDivider)
            setCallback(this@MateChatsFragment)

            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun getPermissionsToRequest(): Array<String>? {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onEndReached() {
        launchPrevChatsLoading()
    }

    private fun launchPrevChatsLoading() {
        mModel.getNextChatChunk()
    }

    override fun onChatPreviewClicked(chatId: Long) {
        navigateToChat(chatId)
    }

    private fun navigateToChat(chatId: Long) {
        val chat = mModel.prepareChatForEntering(chatId)
        val action = MateChatsFragmentDirections
            .actionMateChatsFragmentToMateChatFragment(chat)

        Navigation.findNavController(requireView()).navigate(action)
    }
}