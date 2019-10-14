package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Payload


// To notify the scheduler a device came online
class DeviceOnline(val device: Device): Payload()
// To notify the scheduler a device went offline
class DeviceOffline(val device: Device): Payload()
// A sleep event; device specific
class Sleep: Payload()
// A reboot event; device specific
class Awake: Payload()
// A work request from the scheduler to a device
class WorkRequest(val job: Job): Payload()
// A work completed notification from a device to the scheduler
class WorkCompleted(val device: Device, val job: Job): Payload()
// For the scheduler to periodically check on scheduled jobs
class WorkTimeout(val device: Device, val job: Job): Payload()
// For a scheduler to determine a device shat the bed
class StatusRequestTimeout(val statusRequest: StatusRequest): Payload()
// From the scheduler to know what the status of a device is
class StatusRequest(val device: Device): Payload()
// A response to a status request
class StatusUpdate(val device: Device, val status: Status): Payload()


// Convenience for converting payloads
fun Payload.deviceOnline() = this as? DeviceOnline
fun Payload.deviceOffline() = this as? DeviceOffline
fun Payload.sleep() = this as? Sleep
fun Payload.awake() = this as? Awake
fun Payload.workRequest() = this as? WorkRequest
fun Payload.workCompleted() = this as? WorkCompleted
fun Payload.workTimeout() = this as? WorkTimeout
fun Payload.statusRequestTimeout() = this as? StatusRequestTimeout
fun Payload.statusRequest() = this as? StatusRequest
fun Payload.statusUpdate() = this as? StatusUpdate
