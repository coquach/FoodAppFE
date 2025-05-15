package com.example.foodapp.ui.screen.auth.signup.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.foodapp.BaseViewModel

import com.example.foodapp.data.model.Account

import com.example.foodapp.data.service.AccountService

import com.example.foodapp.utils.StringUtils
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    @ApplicationContext val context: Context,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvents>()
    val event = _event.receiveAsFlow()

    private val _profile = MutableStateFlow(Account())
    val profile = _profile.asStateFlow()

    fun onChangeDisplayName(displayName: String) {
        _profile.update { it.copy(displayName = displayName) }
    }

    fun onChangePhoneNumber(phoneNumber: String) {
        _profile.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun onChangeGender(gender: String) {
        _profile.update { it.copy(gender = gender) }
    }

    fun onChangeDob(dob: LocalDate) {
        _profile.update { it.copy(dob = dob) }
    }

    fun onChangeAvatar(avatar: Uri) {
        _profile.update { it.copy(avatar = avatar) }
    }


    private var isUpdating by mutableStateOf(false)

    fun setMode(mode: Boolean) {
        isUpdating = mode

        if (isUpdating) {
            loadProfile()
        }
    }


    private fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ProfileState.Loading
            try {
                val firebaseUser =
                    Firebase.auth.currentUser ?: throw Exception("User not logged in")
                val uid = firebaseUser.uid
                val docRef = Firebase.firestore.collection("users").document(uid)
                var userDoc = docRef.get().await()

                // Nếu user chưa có document (login bằng Google chẳng hạn)
                if (!userDoc.exists()) {
                    val newUser = mapOf(
                        "id" to firebaseUser.uid,
                        "displayName" to firebaseUser.displayName,
                        "email" to firebaseUser.email,
                        "avatar" to firebaseUser.photoUrl,
                        "phoneNumber" to "",
                        "gender" to "",
                        "dob" to ""
                    )
                    docRef.set(newUser).await()
                    userDoc = docRef.get().await() // load lại sau khi tạo
                }

                val baseAccount = accountService.getUserProfile()
                baseAccount.let {
                    Log.d("FirebaseUser", "UID: ${it.id}")
                    Log.d("FirebaseUser", "Email: ${it.email}")
                    Log.d("FirebaseUser", "DisplayName: ${it.displayName}")
                    Log.d("FirebaseUser", "PhotoURL: ${it.avatar}")
                    Log.d("FirebaseUser", "PhoneNumber: ${it.phoneNumber}")
                }


                _profile.update { it ->
                    it.copy(
                        displayName = baseAccount.displayName,
                        email = baseAccount.email,
                        avatar = baseAccount.avatar,
                        phoneNumber = userDoc.data?.get("phoneNumber") as? String ?: "",
                        gender = userDoc.data?.get("gender") as? String ?: "",
                        dob = (userDoc.data?.get("dob") as? String)?.let {
                            StringUtils.parseLocalDate(
                                it
                            )
                        }
                    )
                }


                _uiState.value = ProfileState.Success

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Lỗi không xác định"
                errorDescription = "${e.message}"
                _uiState.value = ProfileState.Error
            }
        }
    }

    fun updateProfile(isUpdating: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ProfileState.Loading
            try {
                val uid = accountService.currentUserId ?: throw Exception("User not logged in")
                val uri = _profile.value.avatar


                // Chưa xử li thay avatar


                updateFirebaseUserProfile(uri)
                saveUserToFireStore(uid)

                delay(2000L)

                if (isUpdating) {
                    _event.send(ProfileEvents.BackToSetting)
                } else {
                    _event.send(ProfileEvents.ShowDialog)
                }



                _uiState.value = ProfileState.Success
            } catch (e: FirebaseAuthException) {
                e.printStackTrace()
                error = "Lỗi xác thực"
                errorDescription = e.message ?: "Đăng kí thất bại"
                _uiState.value = ProfileState.Error

            } catch (e: FirebaseFirestoreException) {
                e.printStackTrace()
                error = "Lỗi cơ sở dữ liệu"
                errorDescription = e.message ?: "Không thể lưu dữ liệu vào hệ thống"
                _uiState.value = ProfileState.Error

            } catch (e: NullPointerException) {
                e.printStackTrace()
                error = "Thiếu dữ liệu"
                errorDescription = "Một số trường bị null"
                _uiState.value = ProfileState.Error

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Lỗi không xác định"
                errorDescription = e.message ?: "Có gì đó sai sai"
                _uiState.value = ProfileState.Error
            }
        }
    }

    private fun updateFirebaseUserProfile(photoUrl: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfileChangeRequest = userProfileChangeRequest {
                displayName = _profile.value.displayName
                photoUri = photoUrl
            }
            accountService.updateProfile(userProfileChangeRequest)
        }

    }

    private fun saveUserToFireStore(uid: String) {
        val userMap = hashMapOf(
            "id" to _profile.value.id,
            "displayName" to _profile.value.displayName,
            "phoneNumber" to _profile.value.phoneNumber,
            "gender" to _profile.value.gender,
            "dob" to StringUtils.formatLocalDate(_profile.value.dob),
        )
        val firestore = Firebase.firestore

        firestore.collection("users").document(uid)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                val dummyAddress = hashMapOf(
                    "id" to "init",  // để phân biệt là init thôi
                    "formatted" to "",
                    "lat" to 0.0,
                    "lon" to 0.0
                )

                firestore.collection("users")
                    .document(uid)
                    .collection("addresses")
                    .document("init") // tên doc là "init"
                    .set(dummyAddress)
            }
            .addOnFailureListener { e ->
                error = "Lỗi lưu dữ liệu"
                errorDescription = e.message ?: "Không thể lưu dữ liệu vào hệ thống"
                _uiState.value = ProfileState.Error
            }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _event.send(ProfileEvents.NavigateLogin)
        }
    }

    fun onAuthClick() {
        viewModelScope.launch {
            _event.send(ProfileEvents.NavigateAuth)
        }
    }


    sealed class ProfileEvents {
        data object NavigateLogin : ProfileEvents()
        data object NavigateAuth : ProfileEvents()
        data object BackToSetting : ProfileEvents()
        data object ShowDialog : ProfileEvents()
    }

    sealed class ProfileState {
        data object Nothing : ProfileState()
        data object Success : ProfileState()
        data object Error : ProfileState()
        data object Loading : ProfileState()
    }
}