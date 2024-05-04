package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.appbar.MaterialToolbar
import com.qubacy.choosablelistviewlib._common.direction.SwipeDirection
import com.qubacy.choosablelistviewlib.helper.ChoosableListItemTouchHelperCallback
import com.qubacy.choosablelistviewlib.item.animator.ChoosableListItemAnimator
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.component.placeholder.SurfacePlaceholderViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizedFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.operation.handler.AuthorizedUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.operation.handler.InterlocutorUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.toMateRequestItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.operation.handler.MateRequestsUiOperationHandler
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerViewCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateRequestsFragment(

) : BusinessFragment<FragmentMateRequestsBinding, MateRequestsUiState, MateRequestsViewModel>(),
    PermissionRunnerCallback,
    ChoosableListItemTouchHelperCallback.Callback,
    BaseRecyclerViewCallback,
    MateRequestsListAdapterCallback,
    UserBottomSheetViewContainerCallback,
    InterlocutorFragment,
    AuthorizedFragment
{
    companion object {
        const val HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT = 1000L
    }

    @Inject
    @MateRequestsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateRequestsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mPermissionRunner: PermissionRunner<MateRequestsFragment>

    private lateinit var mAdapter: MateRequestsListAdapter

    private lateinit var mHintViewProvider: HintViewProvider
    private lateinit var mSurfacePlaceholderViewProvider: SurfacePlaceholderViewProvider

    private var mInterlocutorDetailsSheet: UserBottomSheetViewContainer? = null

    override fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return super.generateUiOperationHandlers()
            .plus(AuthorizedUiOperationHandler(this))
            .plus(InterlocutorUiOperationHandler(this))
            .plus(MateRequestsUiOperationHandler(this))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMateRequestList()
        initUiControls()
        requestPermissions()

        mInterlocutorDetailsSheet = null

        initSurfacePlaceholderViewProvider()
        initHintViewProvider()
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions || mPermissionRunner.arePermissionsGranted)
            initMateRequests()
    }

    override fun onStop() {
        super.onStop()

        mInterlocutorDetailsSheet?.close()
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentMateRequestsTopBar
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.mateRequestsFragment
    }

    override fun getFragmentTitle(): String {
        return getString(R.string.fragment_mate_requests_top_bar_content_wrapper_title_text)
    }

    private fun initHintViewProvider() {
        mHintViewProvider = HintViewProvider(mBinding.root, false).apply {
            getView().updateLayoutParams<CoordinatorLayout.LayoutParams> {
                anchorId = mBinding.fragmentMateRequestsTopBarWrapper.id
                anchorGravity = Gravity.BOTTOM
                gravity = Gravity.BOTTOM
            }

            mBinding.root.addView(this.getView(), 0)

            setHintText(getString(R.string.fragment_mate_requests_hint_text))
        }

        scheduleHintTextViewAppearanceAnimation()
    }

    private fun initSurfacePlaceholderViewProvider() {
        mSurfacePlaceholderViewProvider = SurfacePlaceholderViewProvider(mBinding.root).apply {
            getView().updateLayoutParams<CoordinatorLayout.LayoutParams> {
                anchorId = R.id.fragment_mate_requests_list
                anchorGravity = Gravity.CENTER
            }

            setText(getString(R.string.fragment_mate_requests_surface_placeholder_text))
            setAnimatedVectorImage(R.drawable.ic_earth_animated)
        }
    }

    private fun initMateRequests() {
        if (mModel.uiState.requests.isNotEmpty()) resetRequestChunks() // todo: is it ok?

        mModel.getNextRequestChunk()
    }

    private fun resetRequestChunks() {
        mModel.resetRequests()
        mAdapter.resetItems()
    }

    private fun requestPermissions() {
        mPermissionRunner = PermissionRunner(this).apply {
            requestPermissions()
        }
    }

    override fun onRequestedPermissionsGranted() {
        initMateRequests()
    }

    override fun runInitWithUiState(uiState: MateRequestsUiState) {
        super.runInitWithUiState(uiState)

        if (uiState.requests.isNotEmpty() && mAdapter.itemCount <= 0)
            initMateRequestListAdapterWithRequests(uiState.requests)
        if (uiState.isLoading) adjustUiWithLoadingState(true)
    }

    override fun adjustUiWithLoadingState(loadingState: Boolean) {
        changeLoadingIndicatorState(loadingState)

        mBinding.fragmentMateRequestsList.setIsEnabled(!loadingState)
    }

    private fun initMateRequestListAdapterWithRequests(requests: List<MateRequestPresentation>) {
        val requestsItemData = requests.map { it.toMateRequestItemData() }

        mAdapter.setMateRequests(requestsItemData)
    }

    fun onMateRequestsFragmentReturnAnsweredRequest(
        position: Int
    ) {
        mBinding.fragmentMateRequestsList.returnSwipedItem(position)
    }

    fun onMateRequestsFragmentRemoveRequest(
        position: Int
    ) {
        mAdapter.removeItemAtPosition(position)

        checkMateRequestListEmpty()
    }

    fun onMateRequestsFragmentInsertRequests(
        requests: List<MateRequestPresentation>,
        position: Int
    ) {
        val mateRequestsData = requests.map { it.toMateRequestItemData() }

        mAdapter.insertMateRequests(mateRequestsData, position)

        checkMateRequestListEmpty()
    }

    fun onMateRequestsFragmentUpdateRequests(
        requests: List<MateRequestPresentation>,
        position: Int
    ) {
        val mateRequestsData = requests.map { it.toMateRequestItemData() }

        mAdapter.updateMateRequests(mateRequestsData, position)

        //checkMateRequestListEmpty()
    }

    fun onMateRequestsFragmentUpdateRequest(
        request: MateRequestPresentation,
        position: Int
    ) {
        val mateRequestData = request.toMateRequestItemData()

        mAdapter.updateMateRequestAtPosition(mateRequestData, position)

        //checkMateRequestListEmpty()
    }

    private fun checkMateRequestListEmpty() {
        val isHintVisible = mAdapter.itemCount <= 0

        changeSurfacePlaceholderVisibility(isHintVisible)
    }

    private fun changeSurfacePlaceholderVisibility(isHintVisible: Boolean) {
        mSurfacePlaceholderViewProvider.setIsVisible(isHintVisible) // todo: mb it'd be better to optimize this;
    }

    override fun adjustInterlocutorFragmentUiWithInterlocutor(interlocutor: UserPresentation) {
        super.adjustInterlocutorFragmentUiWithInterlocutor(interlocutor)

        // todo: changing Mate Request preview..


    }

    private fun initMateRequestList() {
        mAdapter = MateRequestsListAdapter(
            MateRequestItemViewProviderProducer(requireContext()), this)

        mBinding.fragmentMateRequestsList.apply {
            adapter = mAdapter
            itemAnimator = ChoosableListItemAnimator()

            setCallback(this@MateRequestsFragment)
            setItemTouchHelperCallback(ChoosableListItemTouchHelperCallback(
                mCallback = this@MateRequestsFragment))
        }
    }

    private fun initUiControls() {
        initTopBarMenu()
    }

    private fun initTopBarMenu() {
        inflateTopBarMenu()

        mBinding.fragmentMateRequestsTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
    }

    private fun inflateTopBarMenu() {
        mBinding.fragmentMateRequestsTopBar.inflateMenu(R.menu.mate_requests_top_bar)
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.main_top_bar_option_my_profile -> navigateToMyProfile()
            R.id.mate_requests_top_bar_option_hint -> showHint()
            else -> return false
        }

        return true
    }

    private fun showHint() {
        mHintViewProvider.animateAppearance(true)
    }

    private fun navigateToMyProfile() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mateRequestsFragment_to_myProfileFragment)
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mBinding.fragmentMateRequestsTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentMateRequestsList.apply {
            updatePadding(bottom = insets.bottom)
        }
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateRequestsBinding {
        return FragmentMateRequestsBinding.inflate(inflater, container, false)
    }

    override fun onItemSwiped(direction: SwipeDirection, position: Int) {
        mInterlocutorDetailsSheet?.close()

        val isAccepted = (direction == SwipeDirection.RIGHT)

        mModel.answerRequest(position, isAccepted)
    }

    private fun scheduleHintTextViewAppearanceAnimation() {
        mBinding.root.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                mHintViewProvider.scheduleAppearanceAnimation(
                    true, HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT)

                return true
            }
        })
    }

    override fun getInterlocutorDetailsSheet(): UserBottomSheetViewContainer? {
        return mInterlocutorDetailsSheet
    }


    override fun isInterlocutorDetailsMateButtonEnabled(interlocutor: UserPresentation): Boolean {
        return false
    }

    override fun isInterlocutorDetailsMateButtonVisible(interlocutor: UserPresentation): Boolean {
        return false
    }

    override fun initInterlocutorDetailsSheet() {
        mInterlocutorDetailsSheet = createInterlocutorDetailsSheet()
    }

    override fun getInterlocutorDetailsSheetExpandedHeight(): Int {
        val topPosition = mBinding.fragmentMateRequestsTopBarWrapper.bottom
        val bottomPosition = mBinding.root.bottom

        return bottomPosition - topPosition
    }

    override fun getInterlocutorDetailsSheetParent(): CoordinatorLayout {
        return mBinding.root
    }

    override fun getInterlocutorDetailsSheetCallback(): UserBottomSheetViewContainerCallback {
        return this
    }

    override fun onMateRequestClicked(id: Long) {
        val user = mModel.getUserProfileWithMateRequestId(id)

        launchShowUserProfile(user)
    }

    private fun launchShowUserProfile(user: UserPresentation) {
        openInterlocutorDetailsSheet(user)
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMateRequestsProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onEndReached() {
        Log.d(TAG, "onEndReached(): entering..")

        mModel.getNextRequestChunk()
    }

    override fun navigateToLogin() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mateRequestsFragment_to_loginFragment)
    }
}