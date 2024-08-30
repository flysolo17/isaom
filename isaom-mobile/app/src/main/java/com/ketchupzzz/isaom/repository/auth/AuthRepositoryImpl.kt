package com.ketchupzzz.isaom.repository.auth

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ketchupzzz.isaom.utils.UiState
import com.ketchupzzz.isaom.models.FemaleSelection
import com.ketchupzzz.isaom.models.Gender
import com.ketchupzzz.isaom.models.GenderSelection
import com.ketchupzzz.isaom.models.MaleSelection
import com.ketchupzzz.isaom.models.UserType
import com.ketchupzzz.isaom.models.Users
import kotlinx.coroutines.tasks.await


const val USERS_COLLECTION = "users"
class AuthRepositoryImpl(val auth : FirebaseAuth, private val firestore: FirebaseFirestore, val storage: FirebaseStorage) : AuthRepository {
    private var _users: MutableState<Users?> = mutableStateOf(null)

    override fun getUsers(): Users? {
       return  _users.value
    }

    override fun getCurrentUser(result: (UiState<Users>) -> Unit) {
        val user = auth.currentUser ?: return
        result.invoke(UiState.Loading)
        firestore.collection(USERS_COLLECTION).document(user.uid)
            .get()
            .addOnCompleteListener { documentResult ->
                if (documentResult.isSuccessful) {
                    val users = documentResult.result?.toObject(Users::class.java)
                    if (users != null) {
                        result.invoke(UiState.Success(users))
                    } else {
                        auth.signOut()
                        result.invoke(UiState.Error("User data is null"))
                    }
                } else {
                    result.invoke(UiState.Error("Failed to fetch user data"))
                }
            }
    }

    override fun setUser(users: Users?) {
        _users.value = users
    }

    override fun login(email: String, password: String, result: (UiState<Users>) -> Unit) {
        result.invoke(UiState.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val userId = authResult.result?.user?.uid
                    if (userId != null) {
                        firestore.collection(USERS_COLLECTION).document(userId)
                            .get()
                            .addOnCompleteListener { documentResult ->
                                if (documentResult.isSuccessful) {
                                    val user = documentResult.result?.toObject(Users::class.java)
                                    if (user != null) {
                                        result.invoke(UiState.Success(user))
                                    } else {
                                        result.invoke(UiState.Error("User data is null"))
                                    }
                                } else {
                                    result.invoke(UiState.Error("Failed to fetch user data"))
                                }
                            }
                    } else {
                        auth.signOut()
                        result.invoke(UiState.Error("User ID is null"))
                    }
                } else {
                    result.invoke(UiState.Error(authResult.exception?.message ?: "Authentication failed"))
                }
            }
    }


    override fun register(
        name: String,
        sectionID: String,
        type: UserType,
        gender: Gender,
        email: String,
        password: String,
        avatar: String,
        result: (UiState<Users>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                val results = it.result.user
                val newUser = Users(
                    id = results?.uid,
                    name = name,
                    email = email,
                    sectionID = sectionID, gender = gender,
                    type = type,
                    avatar = avatar?: ""
                )
                firestore.collection(USERS_COLLECTION)
                    .document(newUser.id ?: "")
                    .set(newUser)
                    .addOnCompleteListener {
                        result.invoke(UiState.Success(newUser))
                    }.addOnFailureListener {
                        result.invoke(UiState.Error(it.message.toString()))
                    }
            } else {
                result.invoke(UiState.Error("Unknown error"))
            }
        }.addOnFailureListener {
            result.invoke(UiState.Error(it.message.toString()))
        }
    }

    override fun logout(result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        auth.signOut()
        result.invoke(UiState.Success("Successfully Logged Out!"))
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("We've sent a password reset link to $email"))
                } else {
                    result.invoke(UiState.Error(task.exception?.message ?: "Unknown Error"))
                }
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Error(exception.message ?: "Unknown Error"))
            }
    }


    override suspend fun getAllGenderSelection(result: (UiState<GenderSelection>) -> Unit) {
        val maleStorageRef = storage.reference.child("male")
        val femaleStorageRef = storage.reference.child("female")
        Log.d("genders","get")
        val maleUrls = mutableListOf<String>()
        val femaleUrls = mutableListOf<String>()

        val maleTask = maleStorageRef.listAll()
        val femaleTask = femaleStorageRef.listAll()
        Tasks.whenAll(maleTask, femaleTask)
            .addOnSuccessListener {
                val maleItems = maleTask.result?.items
                val femaleItems = femaleTask.result?.items

                val maleUrlTasks = maleItems?.map { it.downloadUrl }
                val femaleUrlTasks = femaleItems?.map { it.downloadUrl }

                Tasks.whenAllSuccess<Uri>(maleUrlTasks ?: emptyList())
                    .addOnSuccessListener { uris ->
                        maleUrls.addAll(uris.map { it.toString() })
                        Tasks.whenAllSuccess<Uri>(femaleUrlTasks ?: emptyList())
                            .addOnSuccessListener { uris ->
                                femaleUrls.addAll(uris.map { it.toString() })
                                val maleSelection = MaleSelection(url = maleUrls)
                                val femaleSelection = FemaleSelection(url = femaleUrls)
                                val genderSelection = GenderSelection(males = maleSelection, females = femaleSelection)
                                Log.d("genders",genderSelection.toString())
                                result(UiState.Success(genderSelection))
                            }
                            .addOnFailureListener { exception ->
                                Log.d("genders",exception.toString())
                                result(UiState.Error(exception.message ?: "Unknown error"))
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("genders",exception.toString())
                        result(UiState.Error(exception.message ?: "Unknown error"))
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("genders",exception.toString())
                result(UiState.Error(exception.message ?: "Unknown error"))
            }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        result: (UiState<String>) -> Unit
    ) {
        try {
            result.invoke(UiState.Loading)
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
                currentUser.reauthenticate(credential).await()
                currentUser.updatePassword(newPassword).await()

                result.invoke(UiState.Success("Password updated successfully."))
            } else {

                result.invoke(UiState.Error("User is not logged in."))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {

            result.invoke(UiState.Error("Old password is incorrect."))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.message.toString()))
        }
    }


}