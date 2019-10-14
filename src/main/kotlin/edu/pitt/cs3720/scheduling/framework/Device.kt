package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.EventListener
import edu.pitt.cs3720.scheduling.framework.des.Payload
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class Device(private val scheduler: Scheduler, private val power: Int, private val failureRate: Float, private val name: String): EventListener {
    private val id: Int

    private var alive = false
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
                    // In which case we'd need to wake up at some point in the future
                    Controller.registerEvent(
                        // Oh, IDK, 2 to 3 minutes...
                        millisFromNow = TimeUnit.SECONDS.toMillis(120 + Random.nextLong(60)),
                        payload = DeviceOnline(device = this),
                        listener = scheduler
                    )
                    // Let's say 1/10 the times the thing fails because the device is dead
                    alive = false
                }
            } else {
                // Schedule the response to the request
                Controller.registerEvent(
                    // I cannot use TimeUnit.SECONDS here because it doesn't have a toMillis(double). SMH
                    millisFromNow = workRequest.job.size * 1000L / power,
                    payload = WorkCompleted(device = this, job = workRequest.job),
                    listener = scheduler
                )
                working = true
            }
        }
        payload.statusRequest()?.let { _ ->
            if (alive) {
                Controller.registerEvent(
                    payload = StatusUpdate(
                        device = this,
                        status = Status(working)
                    ),
                    listener = scheduler
                )
            }
        }
        payload.sleep()?.let { _ ->
            alive = false
            working = false
            Controller.registerEvent(
                payload = DeviceOffline(this),
                listener = scheduler
            )
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
    override fun toString() = name


    companion object {
        private var deviceCount = 0
    }
}
