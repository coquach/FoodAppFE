package com.example.foodapp.token

import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.dto.request.RefreshTokenRequest
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Provider

class TokenInterceptor @Inject constructor(
    private val session: FoodAppSession,
    private val foodApi: Provider<FoodApi> // Lazy để tránh vòng lặp
) : Interceptor {

    private val refreshingToken = AtomicReference<String?>(null)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = session.getAccessToken()

        if (!token.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            response.close()

            val newToken = refreshingToken.get() ?: refreshTokenBlocking()

            return if (!newToken.isNullOrEmpty()) {
                request = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()
                chain.proceed(request)
            } else {
                session.clearTokens()
                response
            }
        }

        return response
    }

    private fun refreshTokenBlocking(): String? {
        return runBlocking {
            refreshTokenSuspend()
        }
    }

    private suspend fun refreshTokenSuspend(): String? {
        val refreshToken = session.getRefreshToken() ?: return null

        return try {
            val refreshResponse =
                safeApiCall { foodApi.get().refreshToken(RefreshTokenRequest(refreshToken)) } // Lazy get()
            if (refreshResponse is ApiResponse.Success) {
                val newAccessToken = refreshResponse.body.data.accessToken
                val newRefreshToken = refreshResponse.body.data.refreshToken

                session.storeToken(newAccessToken, newRefreshToken)
                refreshingToken.set(newAccessToken)
                newAccessToken
            } else {
                session.clearTokens()
                refreshingToken.set(null)
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            session.clearTokens()
            refreshingToken.set(null)
            null
        }
    }
}
