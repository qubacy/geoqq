package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.animation.AccelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.qubacy.choosablelistviewlib._common.direction.SwipeDirection
import com.qubacy.choosablelistviewlib.helper.ChoosableListItemTouchHelperCallback
import com.qubacy.choosablelistviewlib.item.animator.ChoosableListItemAnimator
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui._common.util.view.extension.runVisibilityAnimation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.MateableFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.toMateRequestItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.ReturnAnsweredRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
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
    MateableFragment
{
    companion object {
        const val HINT_TEXT_ANIMATION_DURATION = 300L

        const val HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT = 1000L
        const val HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT = 3000L

        const val DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION = 200L
    }

    @Inject
    @MateRequestsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateRequestsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mPermissionRunner: PermissionRunner<MateRequestsFragment>

    private lateinit var mAdapter: MateRequestsListAdapter

    private var mInterlocutorDetailsSheet: UserBottomSheetViewContainer? = null

    private lateinit var mEmptyListHolderImage: AnimatedVectorDrawableCompat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigationUI(mBinding.fragmentMateRequestsTopBar)

        initMateRequestList()
        initUiControls()
        requestPermissions()

        mInterlocutorDetailsSheet = null
        mEmptyListHolderImage = AnimatedVectorDrawableCompat.create(
            requireContext(), R.drawable.ic_earth_animated)!!

        initEmptyListPlaceholderView()
        scheduleHintTextViewAppearanceAnimation()
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions) initMateRequests()
    }

    override fun onStop() {
        super.onStop()

        mInterlocutorDetailsSheet?.close()
    }

    private fun initEmptyListPlaceholderView() {
        mBinding.fragmentMateRequestsPlaceholderImage.setImageDrawable(mEmptyListHolderImage)

        mBinding.root.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                mEmptyListHolderImage.start()

                return true
            }
        })
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

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        initMateRequests()
    }

    override fun runInitWithUiState(uiState: MateRequestsUiState) {
        super.runInitWithUiState(uiState)

        if (uiState.requests.isNotEmpty() && mAdapter.itemCount <= 0)
            initMateRequestListAdapterWithRequests(uiState.requests)
        if (uiState.isLoading) adjustUiWithLoadingState(true)
    }

    private fun adjustUiWithLoadingState(loadingState: Boolean) {
        changeLoadingIndicatorState(loadingState)

        mBinding.fragmentMateRequestsList.setIsEnabled(!loadingState)
    }

    private fun initMateRequestListAdapterWithRequests(requests: List<MateRequestPresentation>) {
        val requestsItemData = requests.map { it.toMateRequestItemData() }

        mAdapter.setMateRequests(requestsItemData)
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            InsertRequestsUiOperation::class ->
                processInsertRequestsUiOperation(uiOperation as InsertRequestsUiOperation)
            ShowInterlocutorDetailsUiOperation::class ->
                processShowInterlocutorDetailsUiOperation(
                    uiOperation as ShowInterlocutorDetailsUiOperation)
            UpdateInterlocutorDetailsUiOperation::class ->
                processUpdateInterlocutorDetailsUiOperation(
                    uiOperation as UpdateInterlocutorDetailsUiOperation)
            RemoveRequestUiOperation::class ->
                processRemoveRequestUiOperation(uiOperation as RemoveRequestUiOperation)
            ReturnAnsweredRequestUiOperation::class ->
                processReturnAnsweredRequestUiOperation(
                    uiOperation as ReturnAnsweredRequestUiOperation)
            else -> return false
        }

        return true
    }

    private fun processReturnAnsweredRequestUiOperation(
        returnAnsweredRequestUiOperation: ReturnAnsweredRequestUiOperation
    ) {
        mBinding.fragmentMateRequestsList.returnSwipedItem(returnAnsweredRequestUiOperation.position)
    }

    private fun processRemoveRequestUiOperation(
        removeRequestUiOperation: RemoveRequestUiOperation
    ) {
        mAdapter.removeItemAtPosition(removeRequestUiOperation.position)

        checkMateRequestListEmpty()
    }

    private fun processInsertRequestsUiOperation(
        insertRequestsUiOperation: InsertRequestsUiOperation
    ) {
        val mateRequestsData = insertRequestsUiOperation.requests.map { it.toMateRequestItemData() }

        mAdapter.insertMateRequests(mateRequestsData, insertRequestsUiOperation.position)

        checkMateRequestListEmpty()
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

    override fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {
        adjustUiWithLoadingState(loadingOperation.isLoading)
    }

    private fun checkMateRequestListEmpty() {
        setEmptyListPlaceholderVisible(mAdapter.itemCount <= 0) // todo: mb it'd be better to optimize this;
    }

    private fun setEmptyListPlaceholderVisible(isVisible: Boolean) {
        if (isVisible) {
            mBinding.fragmentMateRequestsPlaceholderContainer.runVisibilityAnimation(
                true, DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION)

            mEmptyListHolderImage.start()

        } else {
            mBinding.fragmentMateRequestsPlaceholderContainer.runVisibilityAnimation(
                false, DEFAULT_PLACEHOLDER_VISIBILITY_ANIMATION_DURATION)
        }
    }

    private fun adjustUiWithInterlocutor(interlocutor: UserPresentation) {
        setupInterlocutorDetailsSheet(interlocutor)

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
        mBinding.fragmentMateRequestsTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.main_top_bar_option_my_profile -> navigateToMyProfile()
            else -> return false
        }

        return true
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

    override fun getPermissionsToRequest(): Array<String>? {
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

    private fun animateHintTextView(isAppearing: Boolean) {
        val textViewHeight = mBinding.fragmentMateRequestsTextHint.measuredHeight

        mBinding.fragmentMateRequestsTextHint.apply {
            alpha = if (isAppearing) 0f else 1f
            translationY = if (isAppearing) -textViewHeight.toFloat() else 0f

            if (isAppearing) visibility = View.VISIBLE
        }

        val endAction = {
            mBinding.fragmentMateRequestsTextHint.apply {
                alpha = if (isAppearing) 1f else 0f
                translationY = if (isAppearing) 0f else -textViewHeight.toFloat()

                if (!isAppearing) visibility = View.GONE
                if (isAppearing) launchHintTextViewAnimation(
                    false, HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT)
            }
        }

        mBinding.fragmentMateRequestsTextHint.animate().apply {
            alpha(if (isAppearing) 1f else 0f)
            translationY(if (isAppearing) 0f else -textViewHeight.toFloat())

            duration = HINT_TEXT_ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
        }.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) { endAction() }
            override fun onAnimationEnd(animation: Animator) { endAction() }
        }).start()
    }

    private fun scheduleHintTextViewAppearanceAnimation() {
        mBinding.root.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                launchHintTextViewAnimation(true, HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT)

                return true
            }
        })
    }

    private fun launchHintTextViewAnimation(
        isAppearing: Boolean,
        duration: Long
    ) {
        object : CountDownTimer(duration, duration) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() { animateHintTextView(isAppearing) }
        }.start()
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
}