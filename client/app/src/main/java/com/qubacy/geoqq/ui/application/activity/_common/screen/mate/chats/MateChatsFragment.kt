package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.MateChatsListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MateChatsFragment(

) : BusinessFragment<
    FragmentMateChatsBinding,
    MateChatsUiState,
    MateChatsViewModel
>(), PermissionRunnerCallback {
    @Inject
    @MateChatsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: MateChatsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: MateChatsListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<MateChatsFragment>()
        setupNavigationUI(mBinding.fragmentMateChatsTopBar)
        initMateChatListView()
    }

    override fun onStart() {
        super.onStart()

        mAdapter.addPreviousMateChats(listOf(
            MateChatItemData(0, Uri.EMPTY, "test 1", "Hiiii", 0),
            MateChatItemData(1, Uri.EMPTY, "test 2", "Hiiii", 45),
            MateChatItemData(2, Uri.EMPTY, "test 3", "Hiiii", 105),
        ))
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
    }

    private fun initMateChatListView() {
        mAdapter = MateChatsListAdapter()

        val itemDivider = MaterialDividerItemDecoration(
            requireContext(), MaterialDividerItemDecoration.VERTICAL)

        mBinding.fragmentMateChatsList.apply {
            addItemDecoration(itemDivider)

            adapter = mAdapter
        }
    }

    override fun getPermissionsToRequest(): Array<String>? {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}