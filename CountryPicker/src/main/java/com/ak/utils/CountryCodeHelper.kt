package com.ak.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log

object CountryCodeHelper {
    private const val TAG = "CountryCodeHelper"

    fun getDetectedCountry(context: Context, defaultCountryIsoCode: String): String {
        return detectSIMCountry(context) ?: detectNetworkCountry(context) ?: detectLocaleCountry(
            context
        ) ?: defaultCountryIsoCode
    }

    private fun detectSIMCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            Log.d(TAG, "detectSIMCountry: ${telephonyManager.simCountryIso}")
            return telephonyManager.simCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectNetworkCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            Log.d(TAG, "detectNetworkCountry: ${telephonyManager.networkCountryIso}")
            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectLocaleCountry(context: Context): String? {
        try {
            val localeCountryISO = context.resources.configuration.locales[0].country
            Log.d(TAG, "detectLocaleCountry: $localeCountryISO")
            return localeCountryISO
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}