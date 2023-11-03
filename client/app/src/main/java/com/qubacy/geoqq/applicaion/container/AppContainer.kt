package com.qubacy.geoqq.applicaion.container

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.qubacy.geoqq.applicaion.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.container.signup.SignUpContainer
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase

class AppContainer(
    context: Context
) {
    // Common:

    private val database = Room.databaseBuilder(
        context, Database::class.java, Database.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .createFromAsset(Database.DATABASE_NAME)
        .build()

    // Error:

    private val localErrorDataSource = database.getErrorDAO()

    private val errorDataRepository = ErrorDataRepository(localErrorDataSource)

    // Token:

    private val localTokenDataSource = LocalTokenDataSource(
        context.getSharedPreferences(
            LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, MODE_PRIVATE))
    private val networkTokenDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkTokenDataSource::class.java)

    private val tokenDataRepository = TokenDataRepository(
        localTokenDataSource, networkTokenDataSource)

    // Sign In:

    private val networkSignInDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignInDataSource::class.java)

    private val signInDataRepository = SignInDataRepository(networkSignInDataSource)

    val signInUseCase = SignInUseCase(tokenDataRepository, signInDataRepository, errorDataRepository)

    var signInContainer: SignInContainer? = null

    // Sign Up:

    private val networkSignUpDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignUpDataSource::class.java)

    private val signUpDataRepository = SignUpDataRepository(networkSignUpDataSource)

    val signUpUseCase = SignUpUseCase(tokenDataRepository, signUpDataRepository, errorDataRepository)

    var signUpContainer: SignUpContainer? = null

    // User?:

    private val localUserDataSource = database.getUserDAO()
    private val networkUserDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkUserDataSource::class.java)

    private val userDataRepository = UserDataRepository(localUserDataSource, networkUserDataSource)


}