package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.Payload


class DeviceOnline(val device: Device): Payload
class DeviceOffline(val device: Device): Payload
class Awake: Payload
class WorkRequest(val job: Job): Payload
class WorkCompleted(val device:Device, val job: Job): Payload
class Timeout(val device: Device, val job: Job): Payload
class StatusRequest: Payload
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
