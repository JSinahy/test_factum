package com.itcomca.testfactum.utils

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.itcomca.testfactum.R
import java.text.DateFormat
import java.util.*


class UtilLocation {
    companion object{
        val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

        fun requestingLocationUpdates(context: Context?): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
        }

        fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply()
        }

        fun getLocationText(location: Location?): String {
            return if (location == null) "Unknown location" else "(" + location.getLatitude()
                .toString() + ", " + location.getLongitude().toString() + ")"
        }

        fun getLocationTitle(context: Context): String? {
            return context.getString(
                R.string.location_updated,
                DateFormat.getDateTimeInstance().format(Date())
            )
        }
    }

}