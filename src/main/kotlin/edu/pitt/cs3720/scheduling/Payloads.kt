package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.Payload


// To notify the scheduler a device came online
class DeviceOnline(val device: Device): Payload
// To notify the scheduler a device went offline
class DeviceOffline(val device: Device): Payload
// A reboot event; device specific
class Awake: Payload
// A work request from the scheduler to a device
class WorkRequest(val job: Job): Payload
// A work completed notification from a device to the scheduler
class WorkCompleted(val device:Device, val job: Job): Payload
// For the scheduler to periodically check on scheduled jobs
class Timeout(val device: Device, val job: Job): Payload
// From the scheduler to know what the status of a device is
class StatusRequest: Payload
// A response to a status request
class StatusUpdate(status: Status): Payload


// Convenience for converting payloads
fun Event.deviceOnline() = payload as? DeviceOnline
fun Event.deviceOffline() = payload as? DeviceOffline
fun Event.awake() = payload as? Awake
fun Event.workRequest() = payload as? WorkRequest
fun Event.workCompleted() = payload as? WorkCompleted
fun Event.timeout() = payload as? Timeout
fun Event.statusRequest() = payload as? StatusRequest
fun Event.statusUpdate() = payload as? StatusUpdate
