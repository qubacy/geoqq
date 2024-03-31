package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>() {
    private lateinit var mTopBarMenuPopup: PopupMenu

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigationUI(mBinding.fragmentMyProfileTopBar)
        setupTopBarMenu()
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

    override fun adjustViewToInsets(insets: Insets) {
        super.adjustViewToInsets(insets)

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

    private fun launchLogout() {
        // todo: implement processing..

    }

    private fun launchDeleteProfile() {
        // todo: implement processing..

    }
}