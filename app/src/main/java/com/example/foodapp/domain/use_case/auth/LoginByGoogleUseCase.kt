package com.example.foodapp.domain.use_case.auth

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.foodapp.domain.repository.AccountRepository
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginByGoogleUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(credential: Credential) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                accountRepository.signInWithGoogle(googleIdTokenCredential.idToken)
                emit(FirebaseResult.Success(Unit))
            } else throw IllegalArgumentException("Thông tin không hợp lệ")
        } catch (e: ApiException) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Đăng nhập thất bại"))
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Lỗi xác thực với Firebase"))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Thông tin không hợp lệ"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Lỗi không xác định"))

        }
    }.flowOn(Dispatchers.IO)

}