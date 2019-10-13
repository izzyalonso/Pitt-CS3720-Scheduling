package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Controller
import edu.pitt.cs3720.scheduling.framework.EventListener
import edu.pitt.cs3720.scheduling.framework.Payload
import kotlin.random.Random


class Device(private val scheduler: Scheduler, private val power: Int, private val failureRate: Float): EventListener {
    private val id: Int

    private var alive = true
    private var working = false

    init {
        id = ++deviceCount
    }


    override fun onEvent(payload: Payload) {
        payload.workRequest()?.let { workRequest ->
            val roll = Random.nextFloat()
            if (roll < failureRate) {
                // We are not going to do the work
                working = false
                if (roll < failureRate/10) {
                    // Let's say 1/10 the times the thing fails because the device is dead
                    alive = false
                    // In which case we'd need to wake up at some point in the future
                    Controller.registerEvent(
                        // Oh, IDK, 2 to 3 minutes...
                        millisFromNow = (120 + Random.nextInt(60))*1000,
                        payload = DeviceOnline(device = this),
                        listener = scheduler
                    )
                }
            } else {
                // Schedule the response to the request
                working = true
                Controller.registerEvent(
                    millisFromNow = ((workRequest.job.size.toFloat() / power)*1000).toInt(),
                    payload = WorkCompleted(device = this, job = workRequest.job),
                    listener = scheduler
                )
            }
        }
        payload.statusRequest()?.let { _ ->
            if (alive) {
                Controller.registerEvent(
                    payload = StatusUpdate(device = this, status = Status(working)),
                    listener = scheduler
                )
            }
        }
        payload.awake()?.let { _ ->
            alive = true
            Controller.registerEvent(
                payload = DeviceOnline(this),
                listener = scheduler
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Device) {
            return id == other.id
        }
        return false
    }

    override fun hashCode() = id


    companion object {
        private var deviceCount = 0
    }
}
