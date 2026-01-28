package com.studysmart.core.data.telemetry

import com.studysmart.core.domain.telemetry.TelemetryClient

class MockTelemetryClient : TelemetryClient {
    override fun sendEvent(name: String, params: Map<String, Any?>) { /* no-op for tests */ }
    override fun recordMetric(name: String, value: Double) { /* no-op for tests */ }
}
