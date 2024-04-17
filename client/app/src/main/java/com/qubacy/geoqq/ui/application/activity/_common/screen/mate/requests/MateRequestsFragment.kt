package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import com.qubacy.choosablelistviewlib._common.direction.SwipeDirection
import com.qubacy.choosablelistviewlib.animator.SmoothListItemAnimator
import com.qubacy.choosablelistviewlib.helper.ChoosableListItemTouchHelperCallback
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.MateRequestsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestsFragment(

) : BaseFragment<FragmentMateRequestsBinding>(),
    PermissionRunnerCallback,
    ChoosableListItemTouchHelperCallback.Callback
{
    private lateinit var mAdapter: MateRequestsListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<MateRequestsFragment>()
        setupNavigationUI(mBinding.fragmentMateRequestsTopBar)

        initMateRequestList()
        initUiControls()
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
        mAdapter = MateRequestsListAdapter(MateRequestItemViewProviderProducer(requireContext()))

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


    }
}