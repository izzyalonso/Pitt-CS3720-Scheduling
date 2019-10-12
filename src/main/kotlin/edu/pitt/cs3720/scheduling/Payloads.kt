package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Payload


class DeviceOnline(val device: Device): Payload
class DeviceOffline(val device: Device): Payload
class WorkRequest(val job: Job): Payload
class WorkCompleted(val job: Job): Payload
class Timeout(val job: Job): Payload


// Convenience for converting payloads
fun Payload.deviceOnline() = this as? DeviceOnline
fun Payload.deviceOffline() = this as? DeviceOffline
fun Payload.workRequest() = this as? WorkRequest
fun Payload.workCompleted() = this as? WorkCompleted
fun Payload.timeout() = this as? Timeout
