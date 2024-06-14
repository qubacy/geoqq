package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat

import android.content.Context
import android.graphics.Shader
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui._common.tile.TileDrawable
import com.qubacy.geoqq.ui.application.CustomApplication
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizedFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.operation.handler.AuthorizedUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.ChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.animator.MessageItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.error.type.UiChatErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.operation.handler.ChatUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.util.listener.LocationListener
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.util.listener.LocationListenerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.operation.handler.InterlocutorUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.LocationFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.operation.handler.LocationUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo._common.error.type.UiGeoErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.GeoMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.GeoMessageListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.GeoChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.operation.handler.GeoChatUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessageItemData
import javax.inject.Inject

class GeoChatFragment(

) : BusinessFragment<FragmentGeoChatBinding, GeoChatUiState, GeoChatViewModel>(),
    PermissionRunnerCallback,
    LocationListenerCallback,
    GeoMessageListAdapterCallback,
    UserBottomSheetViewContainerCallback,
    PopupFragment,
    LocationFragment,
    InterlocutorFragment,
    AuthorizedFragment,
    ChatFragment
{
    companion object {
        const val TAG = "GeoChatFragment"
    }

    private val mArgs: GeoChatFragmentArgs by navArgs()

    @Inject
    @GeoChatViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: GeoChatViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: GeoMessageListAdapter

    private lateinit var mPermissionRunner: PermissionRunner<GeoChatFragment>
    private lateinit var mLocationListener: LocationListener

    private var mInterlocutorDetailsSheet: UserBottomSheetViewContainer? = null
    private var mLastWindowInsets: WindowInsetsCompat? = null

    override var messageSnackbar: Snackbar? = null

    override fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return super.generateUiOperationHandlers()
            .plus(GeoChatUiOperationHandler(this))
            .plus(ChatUiOperationHandler(this))
            .plus(LocationUiOperationHandler(this))
            .plus(InterlocutorUiOperationHandler(this))
            .plus(AuthorizedUiOperationHandler(this))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLocationContext() // it's important to run BEFORE super.onStart() & onPermissionGranted();
        initLocationListener()
        initPermissionRunner()

        initMessageListView()
        initUiControls()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as CustomApplication)
            .customApplicationComponent.inject(this)
    }

    override fun getPopupAnchorView(): View {
        return mBinding.fragmentGeoChatInputMessageWrapper
    }

    override fun getPopupFragmentBaseFragment(): BaseFragment<*> {
        return this
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions || mPermissionRunner.arePermissionsGranted)
            mLocationListener.startLocationListening(requireActivity())
    }

    override fun onStop() {
        mLocationListener.reset()

        closePopupMessage()
        closeInterlocutorDetailsSheet()

        super.onStop()
    }

    private fun initLocationListener() {
        mLocationListener = LocationListener(requireContext(), this)
    }

    private fun initPermissionRunner() {
        mPermissionRunner = PermissionRunner(this).also {
            it.requestPermissions(true)
        }
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentGeoChatTopBar
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.geoChatFragment
    }

    override fun getFragmentTitle(): String {
        return getString(R.string.fragment_geo_chat_top_bar_title_text)
    }

    fun onGeoChatFragmentAddGeoMessages(
        messages: List<GeoMessagePresentation>
    ) {
        Log.d(TAG, "onGeoChatFragmentAddGeoMessages(): entering..")

        val localUserId = mModel.getLocalUserId()
        val geoMessageItems = messages.map { it.toGeoMessageItemData(localUserId) }

        mAdapter.addMessages(geoMessageItems)
    }

    fun onGeoChatFragmentUpdateGeoMessages(
        positions: List<Int>,
        messages: List<GeoMessagePresentation>
    ) {
        Log.d(TAG, "onGeoChatFragmentUpdateGeoMessages(): entering..")

        val localUserId = mModel.getLocalUserId()
        val geoMessageItems = messages.map { it.toGeoMessageItemData(localUserId) }

        mAdapter.updateItems(positions, geoMessageItems)
    }

    fun onGeoChatFragmentChangeMessageSending(
        isAllowed: Boolean
    ) {
        adjustUiWithMessageSendingAllowedFlag(isAllowed)
    }

    override fun runInitWithUiState(uiState: GeoChatUiState) {
        super.runInitWithUiState(uiState)

        initUiWithUiState(uiState)
    }

    private fun initLocationContext() {
        if (!mModel.isLocationContextSet())
            mModel.setLocationContext(mArgs.radius, mArgs.longitude, mArgs.latitude)
    }

    private fun initUiWithUiState(uiState: GeoChatUiState) {
        adjustUiWithLoadingState(uiState.isLoading)
        adjustUiWithMessageSendingAllowedFlag(uiState.isMessageSendingAllowed)
    }

    private fun initMessageListView() {
        mAdapter = GeoMessageListAdapter(geoCallback = this)

        mBinding.fragmentGeoChatList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, true
            )
            adapter = mAdapter
            itemAnimator = MessageItemAnimator()
        }

        setupMessageListBackground()
    }

    private fun setupMessageListBackground() {
        val background = ContextCompat.getDrawable(
            requireContext(), R.drawable.chat_background_pattern)!!
        val tileBackground = TileDrawable(background, Shader.TileMode.REPEAT)

        mBinding.fragmentGeoChatList.background = tileBackground
    }

    private fun initUiControls() {
        // todo: implement..
        mBinding.fragmentGeoChatInputMessage.setOnKeyListener { _, keyCode, event ->
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

    override fun viewInsetsToCatch(): Int {
        return super.viewInsetsToCatch() or WindowInsetsCompat.Type.ime()
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mLastWindowInsets = insetsRes

        mBinding.fragmentGeoChatTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentGeoChatInputMessageWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
        mBinding.fragmentGeoChatList.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                this@updateLayoutParams.bottomMargin =
                    mBinding.fragmentGeoChatInputMessageWrapper.measuredHeight
            }
        }

        mInterlocutorDetailsSheet?.apply {
            adjustToInsets(insetsRes)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGeoChatBinding {
        return FragmentGeoChatBinding.inflate(inflater)
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestedPermissionsGranted() {
        mLocationListener.startLocationListening(requireActivity())

        initGeoChat()
    }

    private fun initGeoChat() {
        if (mModel.areMessagesLoaded()) resetMessages()

        mModel.getMessages()
    }

    private fun resetMessages() {
        mAdapter.resetItems()
        mModel.resetMessages()
    }

    override fun onMateButtonClicked(userId: Long) {
        launchAddInterlocutorAsMate(userId)
        mInterlocutorDetailsSheet!!.close()
    }

    private fun launchAddInterlocutorAsMate(userId: Long) {
        mModel.addInterlocutorAsMate(userId)
    }

    private fun launchSendingMessage() {
        val messageText = mBinding.fragmentGeoChatInputMessage.text.toString().trim()

        if (!mModel.isMessageTextValid(messageText))
            return mModel.retrieveError(UiChatErrorType.INVALID_MESSAGE)

        mBinding.fragmentGeoChatInputMessage.text!!.clear()

        mModel.sendMessage(messageText)
    }

    override fun getInterlocutorDetailsSheet(): UserBottomSheetViewContainer? {
        return mInterlocutorDetailsSheet
    }

    override fun isInterlocutorDetailsMateButtonEnabled(
        interlocutor: UserPresentation
    ): Boolean {
        return !interlocutor.isMate
    }

    override fun isInterlocutorDetailsMateButtonVisible(interlocutor: UserPresentation): Boolean {
        Log.d(TAG, "isInterlocutorDetailsMateButtonVisible(): interlocutor.isMate = ${interlocutor.isMate};")

        return !interlocutor.isMate
    }

    override fun initInterlocutorDetailsSheet() {
        mInterlocutorDetailsSheet = createInterlocutorDetailsSheet()
    }

    override fun getInterlocutorDetailsSheetExpandedHeight(): Int {
        val topPosition = mBinding.fragmentGeoChatTopBarWrapper.bottom
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

        mBinding.fragmentGeoChatInputMessage.isEnabled = !isLoading
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentGeoChatProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun adjustUiWithMessageSendingAllowedFlag(isAllowed: Boolean) {
        mBinding.fragmentGeoChatInputMessage.isEnabled = isAllowed
    }

    override fun onGeoMessageClicked(position: Int) {
        mModel.getUserProfileByMessagePosition(position)
    }

    override fun onNewLocationGotten(location: Location?) {
        if (location == null) return

        Log.d(TAG, "onNewLocationGotten(): location = ${location.latitude}:${location.longitude};")

        mModel.changeLastLocation(location)
    }

    override fun onLocationServicesNotEnabled() {
        mModel.retrieveError(UiGeoErrorType.LOCATION_SERVICES_UNAVAILABLE)
    }

    override fun onRequestingLocationUpdatesFailed(exception: Exception) {
        mModel.retrieveError(UiGeoErrorType.LOCATION_REQUEST_FAILED)
    }

    override fun navigateToLogin() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_geoChatFragment_to_loginFragment)
    }

    override fun getPopupFragmentForChatFragment(): PopupFragment {
        return this
    }
}