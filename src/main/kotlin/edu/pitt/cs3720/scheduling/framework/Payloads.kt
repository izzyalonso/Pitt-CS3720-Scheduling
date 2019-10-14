package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Event
import edu.pitt.cs3720.scheduling.framework.des.Payload


// To notify the scheduler a device came online.
data class DeviceOnline(val device: Device): Payload
// To notify the scheduler a device went offline.
data class DeviceOffline(val device: Device): Payload
// A boot event. Signals a device woke up. Includes the device for logging purposes.
data class Awake(val device: Device): Payload
// A shutdown event. Signals a device went to sleep. Includes the device for logging purposes.
data class Sleep(val device: Device): Payload
// A work request from the scheduler to a device. Includes the device for logging.
data class WorkRequest(val device: Device, val job: Job): Payload
// A work completed notification from a device to the scheduler
data class WorkCompleted(val device: Device, val job: Job): Payload
// For the scheduler to periodically check on scheduled jobs
data class WorkTimeout(val device: Device, val job: Job): Payload
// For a scheduler to determine a device shat the bed
data class StatusRequestTimeout(val statusRequest: StatusRequest): Payload
// From the scheduler to know what the status of a device is
data class StatusRequest(val device: Device): Payload
// A response to a status request
data class StatusUpdate(val device: Device, val status: Status): Payload


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


// Convenience for creating certain Events
fun awakeEvent(timeMillis: Long, device: Device) = Event(timeMillis, Awake(device), device)
fun sleepEvent(timeMillis: Long, device: Device) = Event(timeMillis, Sleep(device), device)
