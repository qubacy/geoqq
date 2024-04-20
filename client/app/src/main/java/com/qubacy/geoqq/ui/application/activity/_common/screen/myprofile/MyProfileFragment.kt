package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.input.text.watcher.error.TextInputErrorCleanerWatcher
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator.password.PasswordValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.error.MyProfileErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.DeleteMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.validator.aboutme.AboutMeValidator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyProfileFragment(

) : BusinessFragment<FragmentMyProfileBinding, MyProfileUiState, MyProfileViewModel>(),
    PermissionRunnerCallback
{
    @Inject
    @MyProfileViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MyProfileViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mPickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mTopBarMenuPopup: PopupMenu

    private var mUpdatedAvatarUri: Uri? = null

    override fun initActivityResultLaunchers() {
        mPickImageLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            if (it != null) changeUpdatedAvatarUri(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigationUI(mBinding.fragmentMyProfileTopBar)
        setupTopBarMenu()
        runPermissionCheck<MyProfileFragment>()

        mSnackbarAnchorView = mBinding.fragmentMyProfileButtonUpdate

        initUiControls()
    }

    private fun initUiControls() {
        mBinding.fragmentMyProfileButtonAvatar.setOnClickListener {
            onUpdateAvatarButtonClicked()
        }
        mBinding.fragmentMyProfileInputAboutMe.addTextChangedListener(TextInputErrorCleanerWatcher(
            mBinding.fragmentMyProfileInputAboutMe, mBinding.fragmentMyProfileInputWrapperAboutMe))
        mBinding.fragmentMyProfileInputPassword.addTextChangedListener(TextInputErrorCleanerWatcher(
            mBinding.fragmentMyProfileInputPassword, mBinding.fragmentMyProfileInputWrapperPassword))
        mBinding.fragmentMyProfileInputNewPassword.addTextChangedListener(TextInputErrorCleanerWatcher(
            mBinding.fragmentMyProfileInputNewPassword, mBinding.fragmentMyProfileInputWrapperNewPassword))
        mBinding.fragmentMyProfileInputNewPasswordAgain.addTextChangedListener(TextInputErrorCleanerWatcher(
            mBinding.fragmentMyProfileInputNewPasswordAgain,
            mBinding.fragmentMyProfileInputWrapperNewPasswordAgain
        ))

        mBinding.fragmentMyProfileButtonUpdate.setOnClickListener {
            onUpdateProfileButtonClicked()
        }
    }

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        initMyProfile()
    }

    private fun initMyProfile() {
        if (mModel.uiState.myProfileInputData.isEmpty()) mModel.getMyProfile()
    }

    private fun onUpdateAvatarButtonClicked() {
        pickAvatarImage()
    }

    private fun pickAvatarImage() {
        mPickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun onUpdateProfileButtonClicked() {
        launchUpdateProfile()
    }

    override fun runInitWithUiState(uiState: MyProfileUiState) {
        super.runInitWithUiState(uiState)

        if (uiState.myProfilePresentation != null) initInputsWithUiState(uiState)
    }

    private fun initInputsWithUiState(
        uiState: MyProfileUiState
    ) {
        val myProfilePresentation = uiState.myProfilePresentation!!
        val myProfileInputData = uiState.myProfileInputData

        mBinding.fragmentMyProfileAvatar.apply {
            if (myProfileInputData.avatarUri == null) setImageURI(myProfilePresentation.avatarUri)
            else changeUpdatedAvatarUri(myProfileInputData.avatarUri)
        }
        mBinding.fragmentMyProfileTextUsername.text = myProfilePresentation.username
        mBinding.fragmentMyProfileInputAboutMe.apply {
            if (myProfileInputData.aboutMe == null) setText(myProfilePresentation.aboutMe)
            else setText(myProfileInputData.aboutMe)
        }
        mBinding.fragmentMyProfileInputPassword.apply {
            if (myProfileInputData.password != null) setText(myProfileInputData.password)
        }
        mBinding.fragmentMyProfileInputNewPassword.apply {
            if (myProfileInputData.newPassword != null) setText(myProfileInputData.newPassword)
        }
        mBinding.fragmentMyProfileInputNewPasswordAgain.apply {
            if (myProfileInputData.newPasswordAgain != null)
                setText(myProfileInputData.newPasswordAgain)
        }
        mBinding.fragmentMyProfileSwitchHitMeUp.apply {
            if (myProfileInputData.hitMeUp == null)
                changeHitMeUpInputByHitMeUpType(myProfilePresentation.hitMeUp)
            else changeHitMeUpInputByHitMeUpType(myProfileInputData.hitMeUp)
        }
    }

    private fun changeUpdatedAvatarUri(avatarUri: Uri) {
        mUpdatedAvatarUri = avatarUri

        mBinding.fragmentMyProfileAvatar.setImageURI(avatarUri)
    }

    private fun changeHitMeUpInputByHitMeUpType(hitMeUpType: HitMeUpType) {
        when (hitMeUpType) {
            HitMeUpType.NOBODY -> mBinding.fragmentMyProfileSwitchHitMeUp.isChecked = false
            HitMeUpType.EVERYBODY -> mBinding.fragmentMyProfileSwitchHitMeUp.isChecked = true
            else -> throw IllegalArgumentException()
        }
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            GetMyProfileUiOperation::class ->
                processGetMyProfileOperation(uiOperation as GetMyProfileUiOperation)
            UpdateMyProfileUiOperation::class ->
                processUpdateMyProfileOperation(uiOperation as UpdateMyProfileUiOperation)
            DeleteMyProfileUiOperation::class ->
                processDeleteMyProfileOperation(uiOperation as DeleteMyProfileUiOperation)
            LogoutUiOperation::class ->
                processLogoutOperation(uiOperation as LogoutUiOperation)
            else -> return false
        }

        return true
    }

    private fun processGetMyProfileOperation(getMyProfileOperation: GetMyProfileUiOperation) {
        setupUiWithMyProfilePresentation(getMyProfileOperation.myProfile)
    }

    private fun setupUiWithMyProfilePresentation(myProfilePresentation: MyProfilePresentation) {
        mBinding.fragmentMyProfileAvatar.setImageURI(myProfilePresentation.avatarUri)
        mBinding.fragmentMyProfileTextUsername.text = myProfilePresentation.username
        mBinding.fragmentMyProfileInputAboutMe.setText(myProfilePresentation.aboutMe)

        changeHitMeUpInputByHitMeUpType(myProfilePresentation.hitMeUp)
    }

    private fun processUpdateMyProfileOperation(
        updateMyProfileOperation: UpdateMyProfileUiOperation
    ) {
        onPopupMessageOccurred(R.string.fragment_my_profile_snackbar_message_profile_updated)
    }

    private fun processDeleteMyProfileOperation(
        deleteMyProfileUiOperation: DeleteMyProfileUiOperation
    ) {
        navigateToLogin()
    }

    private fun processLogoutOperation(logoutUiOperation: LogoutUiOperation) {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        Log.d(TAG, "navigateToLogin(): view.tag = " +
                "${requireView().getTag(androidx.navigation.R.id.nav_controller_view_tag)}")
        Log.d(TAG, "navigateToLogin(): navController = ${Navigation.findNavController(requireView())};")

        Navigation.findNavController(requireView())
            .navigate(R.id.action_myProfileFragment_to_loginFragment)
    }

    override fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {
        changeLoadingIndicatorState(loadingOperation.isLoading)
        changeControlsEnabled(!loadingOperation.isLoading)
    }

    override fun onStop() {
        val inputData = getInputData()

        mModel.preserveInputData(inputData)

        super.onStop()
    }

    private fun getInputData(): MyProfileInputData {
        val aboutMe = mBinding.fragmentMyProfileInputAboutMe.text?.toString()
        val password = mBinding.fragmentMyProfileInputPassword.text?.toString()
        val newPassword = mBinding.fragmentMyProfileInputNewPassword.text?.toString()
        val newPasswordAgain = mBinding.fragmentMyProfileInputNewPasswordAgain.text?.toString()
        val hitMeUp = getHitMeUpInputType()

        return MyProfileInputData(
            mUpdatedAvatarUri, aboutMe,
            password, newPassword, newPasswordAgain,
            hitMeUp
        )
    }

    private fun getUpdateData(): MyProfileInputData {
        val myProfilePresentation = mModel.uiState.myProfilePresentation!!
        val inputData = getInputData()

        val avatarUri = inputData.avatarUri.let {
            if (it == myProfilePresentation.avatarUri) null else it
        }
        val aboutMe = inputData.aboutMe.let {
            if (it == myProfilePresentation.aboutMe) null else it
        }
        val password = inputData.password.let {
            if (it.isNullOrEmpty()) null else it
        }
        val newPassword = inputData.newPassword.let {
            if (it.isNullOrEmpty()) null else it
        }
        val newPasswordAgain = inputData.newPasswordAgain.let {
                if (it.isNullOrEmpty()) null else it
            }
        val hitMeUp = inputData.hitMeUp.let {
            if (it == myProfilePresentation.hitMeUp) null else it
        }

        return MyProfileInputData(
            avatarUri, aboutMe,
            password, newPassword, newPasswordAgain,
            hitMeUp
        )
    }

    private fun getHitMeUpInputType(): HitMeUpType {
        return if (mBinding.fragmentMyProfileSwitchHitMeUp.isChecked) HitMeUpType.EVERYBODY
        else HitMeUpType.NOBODY
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding {
        return FragmentMyProfileBinding.inflate(inflater, container, false)
    }

    override fun viewInsetsToCatch(): Int {
        return super.viewInsetsToCatch() or WindowInsetsCompat.Type.ime()
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mBinding.fragmentMyProfileTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.root.apply {
            updatePadding(bottom = insets.bottom)
        }
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentMyProfileProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun changeControlsEnabled(areEnabled: Boolean) {
        mBinding.fragmentMyProfileButtonAvatar.isEnabled = areEnabled
        mBinding.fragmentMyProfileInputAboutMe.isEnabled = areEnabled
        mBinding.fragmentMyProfileInputPassword.isEnabled = areEnabled
        mBinding.fragmentMyProfileInputNewPassword.isEnabled = areEnabled
        mBinding.fragmentMyProfileInputNewPasswordAgain.isEnabled = areEnabled
        mBinding.fragmentMyProfileSwitchHitMeUp.isEnabled = areEnabled
        mBinding.fragmentMyProfileButtonUpdate.isEnabled = areEnabled
    }

    private fun setupTopBarMenu() {
        val expandMenuItem = mBinding.fragmentMyProfileTopBar.menu
            .findItem(R.id.my_profile_top_bar_menu)

        mTopBarMenuPopup = PopupMenu(requireContext(),
            mBinding.fragmentMyProfileTopBarWrapper, Gravity.BOTTOM or Gravity.END
        ).apply {
            setForceShowIcon(true)
            setOnMenuItemClickListener { onTopBarMenuItemClicked(it) }
        }

        mTopBarMenuPopup.menuInflater
            .inflate(R.menu.my_profile_top_bar_popup, mTopBarMenuPopup.menu)

        expandMenuItem.setOnMenuItemClickListener {
            mTopBarMenuPopup.show()

            true
        }
    }

    private fun onTopBarMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.my_profile_top_bar_popup_option_logout -> launchLogout()
            R.id.my_profile_top_bar_popup_option_delete_profile -> launchDeleteProfile()
            else -> return false
        }

        return true
    }

    private fun launchUpdateProfile() {
        val inputData = getUpdateData()

        val invalidInputSet = validateInputs(inputData)

        if (invalidInputSet.isNotEmpty()) return setInputErrors(invalidInputSet)
        if (!mModel.isUpdateDataValid(inputData))
            return mModel.retrieveError(MyProfileErrorType.INVALID_UPDATE_DATA)

        mModel.updateMyProfile(inputData)
        clearInputsAfterUpdate()
    }

    fun validateInputs(inputData: MyProfileInputData): HashSet<Int> {
        val invalidInputSet = hashSetOf<Int>()

        if (inputData.aboutMe != null && !AboutMeValidator().isValid(inputData.aboutMe)) {
            invalidInputSet.add(R.id.fragment_my_profile_input_about_me)
        }

        val passwordValidator = PasswordValidator()

        if (inputData.password != null && !passwordValidator.isValid(inputData.password)) {
            invalidInputSet.add(R.id.fragment_my_profile_input_password)
        }
        if (inputData.newPassword != null && !passwordValidator.isValid(inputData.newPassword)) {
            invalidInputSet.add(R.id.fragment_my_profile_input_new_password)
        }
        if (inputData.newPasswordAgain != null
         && !passwordValidator.isValid(inputData.newPasswordAgain)
        ) {
            invalidInputSet.add(R.id.fragment_my_profile_input_new_password_again)
        }

        return invalidInputSet
    }

    private fun setInputErrors(invalidInputSet: HashSet<Int>) {
        if (invalidInputSet.contains(R.id.fragment_my_profile_input_about_me)) {
            mBinding.fragmentMyProfileInputWrapperAboutMe.error =
                getString(R.string.fragment_my_profile_input_error_about_me)
        }
        if (invalidInputSet.contains(R.id.fragment_my_profile_input_password)) {
            mBinding.fragmentMyProfileInputWrapperPassword.error =
                getString(R.string.fragment_input_error_password)
        }
        if (invalidInputSet.contains(R.id.fragment_my_profile_input_new_password)) {
            mBinding.fragmentMyProfileInputWrapperNewPassword.error =
                getString(R.string.fragment_input_error_password)
        }
        if (invalidInputSet.contains(R.id.fragment_my_profile_input_new_password_again)) {
            mBinding.fragmentMyProfileInputWrapperNewPasswordAgain.error =
                getString(R.string.fragment_input_error_password)
        }
    }

    private fun clearInputsAfterUpdate() {
        mBinding.fragmentMyProfileInputPassword.text?.clear()
        mBinding.fragmentMyProfileInputNewPassword.text?.clear()
        mBinding.fragmentMyProfileInputNewPasswordAgain.text?.clear()
    }

    private fun launchLogout() {
        mModel.logout()
    }

    private fun launchDeleteProfile() {
        showRequestDialog(R.string.fragment_my_profile_dialog_request_message_delete_account, {
            mModel.deleteMyProfile()
        })
    }

    override fun getPermissionsToRequest(): Array<String>? {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}