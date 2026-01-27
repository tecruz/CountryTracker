package com.tecruz.countrytracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Country Tracker.
 * @HiltAndroidApp triggers Hilt's code generation.
 */
@HiltAndroidApp
class CountryTrackerApplication : Application()
