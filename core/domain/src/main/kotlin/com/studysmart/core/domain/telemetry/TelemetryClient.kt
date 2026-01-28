package com.studysmart.core.domain.telemetry

interface TelemetryClient {
    fun sendEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun recordMetric(name: String, value: Double)
}
