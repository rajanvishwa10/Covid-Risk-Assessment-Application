package com.android.covidriskassessmentapplication.firebaseConfig

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.covidriskassessmentapplication.firebaseConfigModel.MainScreenConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson

open class RemoteConfig {


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

        @JvmStatic
        fun getInstance(context: Context): FirebaseRemoteConfig {
            if (mFirebaseRemoteConfig == null) {
                FirebaseApp.initializeApp(context)
                mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(10)
                        .build()

                if (BuildConfig.DEBUG) {
                    mFirebaseRemoteConfig?.setConfigSettingsAsync(configSettings)
                }
                mFirebaseRemoteConfig?.setDefaultsAsync(RemoteConfigDefaults.defaultMap)

                mFirebaseRemoteConfig?.fetchAndActivate()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i("Config sync successful", "")
                            } else {
                                print("Config sync failed")

                            }
                        }

            }
            return mFirebaseRemoteConfig!!
        }


        @JvmStatic
        fun getCityConfig(context: Context): MainScreenConfig {
            var json = getInstance(context).getString(RemoteConfigDefaults.CITIES_CONFIG.key())
            if (json.isEmpty()) {
                json = RemoteConfigDefaults.CITIES_CONFIG.value().toString()
            }
            return Gson().fromJson(json, MainScreenConfig::class.java)
        }



    }
}
