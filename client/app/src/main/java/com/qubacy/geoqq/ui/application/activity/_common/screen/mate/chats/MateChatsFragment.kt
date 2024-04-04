package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.graphics.Insets
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.view.BaseRecyclerViewCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.toMateChatItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatsFragment(

) : BusinessFragment<
    FragmentMateChatsBinding,
    MateChatsUiState,
    MateChatsViewModel
>(), PermissionRunnerCallback, BaseRecyclerViewCallback {
    @Inject
    @MateChatsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateChatsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateChatsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(owner = this) { }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<MateChatsFragment>()
        setupNavigationUI(mBinding.fragmentMateChatsTopBar)
        initMateChatListView()

        mBinding.fragmentMateChatsTopBar.setOnMenuItemClickListener {
            onTopBarMenuItemClicked(it)
        }
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

        if (uiState.chatChunks.isNotEmpty()) initMateChatsWithChatChunks(uiState.chatChunks)
        if (uiState.isLoading) changeLoadingIndicatorState(true)
    }

    private fun initMateChatsWithChatChunks(chatChunks: Map<Int, List<MateChatPresentation>>) {
        val chats = chatChunks.flatMap {
            it.value.map { chatList -> chatList.toMateChatItemData() }
        }

        mAdapter.setMateChats(chats)
    }

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        initMateChats()
    }

    private fun initMateChats() {
        if (mModel.uiState.chatChunks.isEmpty()) mModel.getNextChatChunk()
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
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateChatsBinding {
        return FragmentMateChatsBinding.inflate(inflater, container, false)
    }

    override fun adjustViewToInsets(insets: Insets) {
        super.adjustViewToInsets(insets)

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
        mAdapter = MateChatsListAdapter()

        val itemDivider = MaterialDividerItemDecoration(
            requireContext(), MaterialDividerItemDecoration.VERTICAL)

        mBinding.fragmentMateChatsList.apply {
            addItemDecoration(itemDivider)
            setCallback(this@MateChatsFragment)

            adapter = mAdapter
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
}