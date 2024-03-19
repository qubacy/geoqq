package com.qubacy.geoqq.domain._common.usecase

import com.qubacy.geoqq._common._test._common.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito

abstract class UseCaseTest<UseCaseType : UseCase>() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected lateinit var mUseCase: UseCaseType

    protected var mGetErrorResult: Error? = null

    @Before
    open fun setup() {
        init()
    }

    @After
    open fun clear() {
        mGetErrorResult = null
    }

    private fun init() {
        val repositories = initRepositories()

        initUseCase(repositories)

        mUseCase.setCoroutineDispatcher(Dispatchers.Main)
    }

    protected open fun initRepositories(): List<DataRepository> {
        val errorDataRepositoryMock = Mockito.mock(ErrorDataRepository::class.java)

        Mockito.`when`(errorDataRepositoryMock.getError(Mockito.anyLong()))
            .thenAnswer{ mGetErrorResult }

        return listOf(errorDataRepositoryMock)
    }

    protected abstract fun initUseCase(repositories: List<DataRepository>)
}