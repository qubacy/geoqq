package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.placeholder.SurfacePlaceholderViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizedFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.operation.handler.AuthorizedUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.operation.handler.MateChatsUiOperationHandler
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerViewCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatsFragment(

) : BusinessFragment<
    FragmentMateChatsBinding,
        MateChatsUiState,
        MateChatsViewModel
>(),
    AuthorizedFragment,
    PermissionRunnerCallback,
    BaseRecyclerViewCallback,
    MateChatsListAdapterCallback
{
    @Inject
    @MateChatsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateChatsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateChatsListAdapter
    private lateinit var mPermissionRunner: PermissionRunner<MateChatsFragment>

    private lateinit var mSurfacePlaceholderViewProvider: SurfacePlaceholderViewProvider

    override fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return super.generateUiOperationHandlers()
            .plus(AuthorizedUiOperationHandler(this))
            .plus(MateChatsUiOperationHandler(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(owner = this) { }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMateChatListView()
        requestChatPermissions()

        mBinding.fragmentMateChatsTopBar.setOnMenuItemClickListener {
            onTopBarMenuItemClicked(it)
        }

        initSurfacePlaceholderViewProvider()
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentMateChatsTopBar
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.mateChatsFragment
    }

    override fun getFragmentTitle(): String {
        return getString(R.string.fragment_mate_chats_top_bar_title_text)
    }

    private fun initSurfacePlaceholderViewProvider() {
        mSurfacePlaceholderViewProvider = SurfacePlaceholderViewProvider(mBinding.root).apply {
            getView().updateLayoutParams<CoordinatorLayout.LayoutParams> {
                anchorId = R.id.fragment_mate_chats_list
                anchorGravity = Gravity.CENTER
            }

            setText(getString(R.string.fragment_mate_chats_surface_placeholder_text))
            setAnimatedVectorImage(R.drawable.ic_earth_animated)
        }
    }

    private fun checkMateChatListEmpty() {
        mSurfacePlaceholderViewProvider.setIsVisible(mAdapter.itemCount <= 0) // todo: mb it'd be better to optimize this;
    }

    private fun requestChatPermissions() {
        mPermissionRunner = PermissionRunner(this).apply {
            requestPermissions()
        }
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions || mPermissionRunner.arePermissionsGranted)
            initMateChats()
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

    override fun onRequestedPermissionsGranted() {
        initMateChats()
    }

    private fun initMateChats() {
        if (mModel.areChatChunksInitialized()) resetChatChunks()

        mModel.getNextChatChunk()
    }

    private fun resetChatChunks() {
        mModel.resetChatChunks()
        mAdapter.resetItems()
    }

    fun onMateChatsFragmentInsertChats(
        chats: List<MateChatPresentation>,
        position: Int
    ) {
        val chatItems = chats.map { it.toMateChatItemData() }

        mAdapter.insertMateChats(chatItems, position)

        checkMateChatListEmpty()
    }

    fun onMateChatsFragmentUpdateChats(
        chats: List<MateChatPresentation>,
        position: Int,
        chatChunkSizeDelta: Int
    ) {
        val chatItems = chats.map { it.toMateChatItemData() }

        if (chatChunkSizeDelta < 0) {
            val itemsToInsertCount = -chatChunkSizeDelta
            val itemsToUpdateCount = chats.size - itemsToInsertCount

            val itemsToInsert = chatItems.subList(itemsToUpdateCount, chats.size)
            val itemsToUpdate = chatItems.subList(0, itemsToUpdateCount)

            mAdapter.insertMateChats(itemsToInsert, itemsToUpdateCount)
            mAdapter.updateMateChatsChunk(itemsToUpdate, 0)

        } else {
            mAdapter.updateMateChatsChunk(chatItems, position)

            if (chatChunkSizeDelta > 0) {
                mAdapter.deleteMateChats(position + chats.size, chatChunkSizeDelta)
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

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMateChatsProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    override fun adjustUiWithLoadingState(loadingState: Boolean) {
        changeLoadingIndicatorState(loadingState)

        mBinding.fragmentMateChatsList.setIsEnabled(!loadingState)
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

    override fun getPermissionsToRequest(): Array<String> {
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

    override fun navigateToLogin() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mateChatsFragment_to_loginFragment)
    }
}