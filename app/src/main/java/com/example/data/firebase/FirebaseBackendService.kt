package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseBackendService(context: Context) {

    private val instances = FirebaseConfig.getOrInitializeFirebase(context)
    val auth: FirebaseAuth? = instances.first
    val firestore: FirebaseFirestore? = instances.second

    fun isFirebaseAvailable(): Boolean = auth != null

    suspend fun registerOrLoginInFirebaseAuth(
        email: String,
        name: String,
        password: String = "GoogleStudent@2026"
    ): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not ready."))
        val safePassword = if (password.length >= 6) password else "${password}123456".take(8)
        val cleanEmail = email.trim().lowercase()

        return try {
            val user = try {
                val res = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, safePassword).await()
                res.user
            } catch (collision: Exception) {
                try {
                    val res = firebaseAuth.signInWithEmailAndPassword(cleanEmail, safePassword).await()
                    res.user
                } catch (signInErr: Exception) {
                    firebaseAuth.currentUser
                }
            }

            try {
                user?.updateProfile(
                    userProfileChangeRequest {
                        displayName = name
                    }
                )?.await()
            } catch (pe: Exception) {
                Log.w("FirebaseBackend", "Profile update non-fatal: ${pe.message}")
            }

            Log.d("FirebaseBackend", "Firebase Auth registered/signed-in: $cleanEmail, uid: ${user?.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseBackend", "Firebase Auth registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signUpWithFirebase(
        name: String,
        email: String,
        password: String,
        college: String,
        branch: String
    ): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not ready."))
        val safePassword = if (password.length >= 6) password else "${password}123456".take(8)
        val cleanEmail = email.trim().lowercase()

        return try {
            val user = withTimeoutOrNull(10000L) {
                val createdUser = try {
                    val authResult = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, safePassword).await()
                    authResult.user
                } catch (collision: Exception) {
                    val authResult = firebaseAuth.signInWithEmailAndPassword(cleanEmail, safePassword).await()
                    authResult.user
                }

                try {
                    createdUser?.updateProfile(
                        userProfileChangeRequest {
                            displayName = name
                        }
                    )?.await()
                } catch (pe: Exception) {
                    Log.w("FirebaseBackend", "Profile update non-fatal: ${pe.message}")
                }
                createdUser
            }

            // Sync immediately to Firestore
            saveUserProfileToFirestore(
                email = cleanEmail,
                name = name,
                college = college,
                branch = branch,
                xp = 0,
                streak = 1
            )

            Log.d("FirebaseBackend", "Sign up success for $cleanEmail")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseBackend", "Sign up error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithFirebase(email: String, password: String): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not ready."))

        return try {
            val user = withTimeoutOrNull(6000L) {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                authResult.user
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseBackend", "Sign in error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String, defaultName: String? = null): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth not ready."))
        return try {
            val user = withTimeoutOrNull(7000L) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val loggedUser = authResult.user

                loggedUser?.email?.let { email ->
                    val displayName = loggedUser.displayName ?: defaultName ?: email.substringBefore("@")
                    CoroutineScope(Dispatchers.IO).launch {
                        saveUserProfileToFirestore(
                            email = email,
                            name = displayName,
                            college = "National Institute of Technology",
                            branch = "Computer Science & Engineering"
                        )
                    }
                }
                loggedUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseBackend", "Google sign-in token error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchFirestoreUserProfile(email: String): Map<String, Any>? {
        val db = firestore ?: return null
        return try {
            withTimeoutOrNull(4000L) {
                val doc = db.collection("users").document(email.lowercase()).get().await()
                if (doc.exists()) doc.data else null
            }
        } catch (e: Exception) {
            Log.w("FirebaseBackend", "Firestore fetch non-fatal: ${e.message}")
            null
        }
    }

    suspend fun saveUserProfileToFirestore(
        email: String,
        name: String,
        college: String,
        branch: String,
        xp: Int = 0,
        streak: Int = 1,
        avatarEmoji: String = "🧑‍💻"
    ) {
        val db = firestore ?: run {
            Log.w("FirebaseBackend", "Firestore instance is null, skipping Firestore save.")
            return
        }
        val cleanEmail = email.trim().lowercase()

        // Ensure active auth session so Firestore security rules allow the write
        if (auth?.currentUser == null) {
            try {
                auth?.signInAnonymously()?.await()
                Log.d("FirebaseBackend", "Anonymous auth established for Firestore write")
            } catch (e: Exception) {
                Log.w("FirebaseBackend", "Anonymous auth non-fatal: ${e.message}")
            }
        }

        try {
            withTimeoutOrNull(8000L) {
                val data = hashMapOf(
                    "name" to name,
                    "email" to cleanEmail,
                    "college" to college,
                    "branch" to branch,
                    "avatarEmoji" to avatarEmoji,
                    "xp" to xp,
                    "streak" to streak,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("users").document(cleanEmail).set(data, SetOptions.merge()).await()
                Log.d("FirebaseBackend", "SUCCESS: Document saved in Firestore 'users/$cleanEmail'")
            }
        } catch (e: Exception) {
            Log.w("FirebaseBackend", "Firestore save exception: ${e.message}")
        }
    }

    fun recordQuestToFirestore(
        email: String,
        topicName: String,
        scoreEarned: Int,
        totalQuestions: Int,
        xpEarned: Int
    ) {
        val db = firestore ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withTimeoutOrNull(5000L) {
                    val record = hashMapOf(
                        "topicName" to topicName,
                        "score" to scoreEarned,
                        "totalQuestions" to totalQuestions,
                        "xpEarned" to xpEarned,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("users")
                        .document(email.lowercase())
                        .collection("completed_quests")
                        .add(record)
                        .await()

                    val userRef = db.collection("users").document(email.lowercase())
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(userRef)
                        val currentXp = snapshot.getLong("xp") ?: 120L
                        val currentStreak = snapshot.getLong("streak") ?: 3L
                        transaction.update(userRef, "xp", currentXp + xpEarned)
                        transaction.update(userRef, "streak", currentStreak + 1L)
                    }.await()
                }
            } catch (e: Exception) {
                Log.w("FirebaseBackend", "Firestore quest record non-fatal: ${e.message}")
            }
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("FirebaseBackend", "Sign out error: ${e.message}")
        }
    }

    suspend fun fetchFirestoreLeaderboard(): List<Map<String, Any>> {
        val db = firestore ?: return emptyList()
        return try {
            withTimeoutOrNull(4000L) {
                val snapshot = db.collection("users")
                    .limit(25)
                    .get()
                    .await()
                snapshot.documents.mapNotNull { it.data }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth?.currentUser
}
