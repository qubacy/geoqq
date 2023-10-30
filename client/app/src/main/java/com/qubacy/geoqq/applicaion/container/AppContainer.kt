package com.qubacy.geoqq.applicaion.container

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.qubacy.geoqq.applicaion.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.container.signup.SignUpContainer
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase

class AppContainer(
    context: Context
) {
    private val localTokenDataSource = LocalTokenDataSource(
        context.getSharedPreferences(
            LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, MODE_PRIVATE))
    private val networkTokenDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkTokenDataSource::class.java)

    private val tokenDataRepository = TokenDataRepository(
        localTokenDataSource, networkTokenDataSource)

    private val networkSignInDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignInDataSource::class.java)

    private val signInDataRepository = SignInDataRepository(
        tokenDataRepository, networkSignInDataSource)


    val signInUseCase = SignInUseCase(signInDataRepository)

    var signInContainer: SignInContainer? = null

    private val networkSignUpDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignUpDataSource::class.java)

    private val signUpDataRepository = SignUpDataRepository(
        tokenDataRepository, networkSignUpDataSource)

    val signUpUseCase = SignUpUseCase(signUpDataRepository)

    var signUpContainer: SignUpContainer? = null
}