package com.qubacy.geoqq.ui.screen.myprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.MainActivity
import com.qubacy.geoqq.ui.common.fragment.BaseFragment
import com.qubacy.geoqq.ui.common.component.combobox.adapter.ComboBoxAdapter
import com.qubacy.geoqq.ui.common.component.combobox.view.ComboBoxView.Companion.POSITION_NOT_DEFINED
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory

class MyProfileFragment() : BaseFragment() {
    companion object {
        const val TAG = "MyProfileFragment"

        const val PRIVACY_HIT_UP_POSITION_KEY = "privacyHitUpPosition"
        const val USER_AVATAR_URI_KEY = "userAvatarUri"
    }

    override val mModel: MyProfileViewModel by viewModels {
        MyProfileViewModelFactory()
    }

    private lateinit var mBinding: FragmentMyProfileBinding

    private lateinit var mPrivacyHitUpAdapter: ArrayAdapter<String>

    private var mPrivacyHitUpPosition: Int = POSITION_NOT_DEFINED
    private var mUserAvatarUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun retrieveSavedInstanceState(savedInstanceState: Bundle) {
        mUserAvatarUri = savedInstanceState.getString(USER_AVATAR_URI_KEY)?.toUri()
        val privacyHitUpPosition = savedInstanceState.getInt(PRIVACY_HIT_UP_POSITION_KEY)

        mBinding.userAvatar.setImageURI(mUserAvatarUri)
        changePrivacyHitUpPosition(privacyHitUpPosition)
//        mBinding.privacyHitUp.currentItemPosition = mPrivacyHitUpPosition
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(USER_AVATAR_URI_KEY, mUserAvatarUri?.toString())
        outState.putInt(PRIVACY_HIT_UP_POSITION_KEY, mPrivacyHitUpPosition)

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_my_profile,
            container,
            false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.uploadAvatarButton.setOnClickListener {
            onUploadAvatarButtonClicked()
        }
        mBinding.confirmButton.setOnClickListener {
            onConfirmButtonClicked()
        }

        mPrivacyHitUpAdapter = ComboBoxAdapter(
            requireContext(),
            R.layout.component_large_autofill_input_dropdown_option,
            resources.getStringArray(R.array.hit_up_variants))

        mBinding.privacyHitUp.apply {
            setDropDownBackgroundResource(R.drawable.component_autofill_input_dropdown_background)
            onItemClickListener =
                OnItemClickListener {
                        parent, view, position, id -> onPrivacyHitUpItemSelected(position)
                }

            setAdapter(mPrivacyHitUpAdapter)
        }

        if (savedInstanceState != null) {
            retrieveSavedInstanceState(savedInstanceState)
        }

        if (mPrivacyHitUpPosition == POSITION_NOT_DEFINED) {
            changePrivacyHitUpPosition(0)
        }
    }

    private fun changePrivacyHitUpPosition(newPosition: Int) {
        mBinding.privacyHitUp.currentItemPosition = newPosition
        mPrivacyHitUpPosition = newPosition
    }

    private fun onPrivacyHitUpItemSelected(position: Int) {
        Log.d(TAG, "onPrivacyHitUpItemSelected(): position = $position")

        mPrivacyHitUpPosition = position
    }

    private fun onUploadAvatarButtonClicked() {
        (requireActivity() as MainActivity).pickImage {
            if (it == null) return@pickImage

            Log.d(TAG, "onUploadAvatarButtonClicked(): pickedImgUri: ${it.toString()}")

            mUserAvatarUri = it
            mBinding.userAvatar.setImageURI(it)
        }
    }

    private fun onConfirmButtonClicked() {
        if (areInputsEmpty()) {
            showMessage(R.string.error_my_profile_data_incorrect)

            return
        }

        // todo: conveying data to the model..


    }

    private fun areInputsEmpty(): Boolean {
        val usernameText = mBinding.usernameInput.input.text.toString()
        val aboutMeText = mBinding.aboutMeInput.input.text.toString()
        val passwordText = mBinding.passwordInput.input.text.toString()
        val passwordConfirmationText = mBinding.passwordConfirmationInput.input.text.toString()

        return (usernameText.isEmpty() || aboutMeText.isEmpty() || passwordText.isEmpty()
             || passwordConfirmationText.isEmpty() || isPrivacyHitUpEmpty())
    }

    private fun isPrivacyHitUpEmpty(): Boolean {
        return (mPrivacyHitUpPosition !in 0 until mPrivacyHitUpAdapter.count)
    }

    override fun handleError(error: Error) {
        TODO("Not yet implemented")


    }

}