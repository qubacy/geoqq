package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.recyclerview.widget.ItemTouchHelper
import com.qubacy.choosablelistviewlib._common.direction.SwipeDirection
import com.qubacy.choosablelistviewlib.animator.SmoothListItemAnimator
import com.qubacy.choosablelistviewlib.helper.ChoosableListItemTouchHelperCallback
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.MateableFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateRequestsFragment(

) : BusinessFragment<FragmentMateRequestsBinding, MateRequestsUiState, MateRequestsViewModel>(),
    PermissionRunnerCallback,
    ChoosableListItemTouchHelperCallback.Callback,
    MateRequestsListAdapterCallback,
    UserBottomSheetViewContainerCallback,
    MateableFragment
{
    companion object {
        const val HINT_TEXT_ANIMATION_DURATION = 300L

        const val HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT = 1000L
        const val HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT = 3000L
    }

    @Inject
    @MateRequestsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateRequestsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateRequestsListAdapter

    private lateinit var mInterlocutorDetailsSheet: UserBottomSheetViewContainer

    private var mLastWindowInsets: WindowInsetsCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<MateRequestsFragment>()
        setupNavigationUI(mBinding.fragmentMateRequestsTopBar)

        initMateRequestList()
        initUiControls()

        scheduleHintTextViewAppearanceAnimation()
    }

    override fun onStart() {
        super.onStart()

        // todo: delete:
        val uri = Uri.parse("android.resource://com.qubacy.geoqq/drawable/ic_launcher_background")
        mAdapter.addItem(MateRequestItemData(0, uri, "test"))
        mAdapter.addItem(MateRequestItemData(1, uri, "test"))
        mAdapter.addItem(MateRequestItemData(2, uri, "test"))
    }

    private fun initMateRequestList() {
        mAdapter = MateRequestsListAdapter(
            MateRequestItemViewProviderProducer(requireContext()), this)

        mBinding.fragmentMateRequestsList.apply {
            adapter = mAdapter
            itemAnimator = SmoothListItemAnimator()

            ItemTouchHelper(ChoosableListItemTouchHelperCallback(
                mCallback = this@MateRequestsFragment)).attachToRecyclerView(this)
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

        mLastWindowInsets = insetsRes

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
        // todo: implement..

        // todo: delete:
        mAdapter.removeItemAtPosition(position)
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
        return true
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

    override fun getInterlocutorDetailsSheetInsets(): WindowInsetsCompat {
        return mLastWindowInsets!!
    }

    override fun onMateButtonClicked() {
        TODO("Not yet implemented")

        // todo: is it ok to preserve the button in the fragment?
    }

    override fun onMateRequestClicked(id: Long) {
        val user = mModel.getUserProfileWithMateRequestId(id)

        launchShowUserProfile(user)
    }

    private fun launchShowUserProfile(user: UserPresentation) {
        openInterlocutorDetailsSheet(user)
    }
}