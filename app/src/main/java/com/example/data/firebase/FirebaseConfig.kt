package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {

    private const val TAG = "FirebaseConfig"

    fun getOrInitializeFirebase(context: Context): Pair<FirebaseAuth?, FirebaseFirestore?> {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val initialized = try {
                    FirebaseApp.initializeApp(context.applicationContext) != null
                } catch (e: Exception) {
                    Log.w(TAG, "Default initializeApp failed: ${e.message}")
                    false
                }

                if (!initialized && FirebaseApp.getApps(context).isEmpty()) {
                    // Explicit fallback to configured project credentials from google-services.json
                    val options = FirebaseOptions.Builder()
                        .setApiKey("AIzaSyDUoJM4gKx37T89r8wZvWa0dmaVB2PIgk0")
                        .setApplicationId("1:284432790537:android:316a6e0e6fa47ed67bf4d8")
                        .setProjectId("self-ai-e63f6")
                        .setStorageBucket("self-ai-e63f6.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(context.applicationContext, options)
                    Log.d(TAG, "Firebase initialized with self-ai-e63f6 project options")
                } else {
                    Log.d(TAG, "Firebase initialized from google-services.json successfully")
                }
            }

            val auth = try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseAuth not available: ${e.message}")
                null
            }

            val firestore = try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseFirestore not available: ${e.message}")
                null
            }

            return Pair(auth, firestore)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error: ${e.message}")
            return Pair(null, null)
        }
    }
}
