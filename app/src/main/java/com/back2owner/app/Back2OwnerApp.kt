package com.back2owner.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class with Hilt dependency injection setup
 */
@HiltAndroidApp
class Back2OwnerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global app configuration here
    }
}
