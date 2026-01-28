package com.studysmart.core.data.telemetry

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.studysmart.core.domain.telemetry.TelemetryClient

class FirebaseTelemetryAdapter(private val analytics: FirebaseAnalytics) : TelemetryClient {
    override fun sendEvent(name: String, params: Map<String, Any?>) {
        val bundle = Bundle()
        params.forEach { (k, v) ->
            if (v != null) {
                when (v) {
                    is String -> bundle.putString(k, v)
                    is Int -> bundle.putInt(k, v)
                    is Long -> bundle.putLong(k, v)
                    is Double -> bundle.putDouble(k, v)
                    is Boolean -> bundle.putBoolean(k, v)
                    else -> bundle.putString(k, v.toString())
                }
            }
        }
        analytics.logEvent(name, bundle)
    }

    override fun recordMetric(name: String, value: Double) {
        val bundle = Bundle().apply {
            putDouble("value", value)
        }
        analytics.logEvent("${name}_metric", bundle)
    }
}
