package com.qubacy.geoqq.domain._common.usecase.base

import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

abstract class UseCaseTest<UseCaseType : UseCase>() {
    @get:Rule
    open val rule = RuleChain.outerRule(MainDispatcherRule())

    protected lateinit var mUseCase: UseCaseType

    protected lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    @Before
    open fun setup() {
        init()
    }

    @After
    open fun clear() {
        mErrorDataSourceMockContainer.reset()
    }

    private fun init() {
        val dependencies = initDependencies()

        initUseCase(dependencies)

        mUseCase.setCoroutineDispatcher(Dispatchers.Main)
    }

    protected open fun initDependencies(): List<Any> {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        return listOf(mErrorDataSourceMockContainer.errorDataSourceMock)
    }

    protected abstract fun initUseCase(dependencies: List<Any>)
}