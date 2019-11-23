package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.EventListener
import edu.pitt.cs3720.scheduling.framework.des.Payload
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/**
 * A simulated device. Has the following parameters:
 *
 * - [capability] a representation of the amount of work a device can get done per unit time.
 * - [efficiency] a representation of how much power gets converted to usable work compared to a baseline.
 * - [failureRate] the probability a device will fail. Failures come in two flavors:
 *      * The message to notify the work was completed got somehow lost.
 *      * The device crashed. It either bricked or lost power. Will recover within two to three minutes.
 *
 * Notes:
 *
 * - The device takes the [scheduler] as a parameter because the scheduler will need to be set as the listener for
 *   events generated by this device.
 * - The device's [name] will be the string representation of the device. Choose it wisely.
 *
 * @author Ismael Alonso
 */
class Device(private val scheduler: Scheduler,
             public val capability: Int,
             public val efficiency: Float,
             public val failureRate: Float,
             private val name: String): EventListener {

    private val id: Int

    // Used to keep track of whether the device is alive at any point
    private var alive = false
    // Used to calculate whether the device is doing work or not
    private var workScheduledUntil: Long = -1

    init {
        id = ++deviceCount
    }


    override fun onEvent(payload: Payload) {
        payload.workRequest()?.let { workRequest ->
            // I cannot use TimeUnit.SECONDS here because it doesn't have a toMillis(float/double). SMH
            val workTimeMillis = workRequest.job.size * 1000L / capability
            // If the device
            workScheduledUntil = Controller.currentTimeMillis() + workTimeMillis
            val roll = Random.nextFloat()
            when {
                roll > failureRate -> // Schedule the response to the request
                    Controller.registerEvent(
                        millisFromNow = workTimeMillis,
                        payload = WorkCompleted(device = this, job = workRequest.job),
                        listener = scheduler
                    )
                roll < failureRate/10 -> { // Let's say 1/10 the times the thing fails because the device just died
                    alive = false
                    workScheduledUntil = -1
                    // In which case we'd need to wake up at some point in the future
                    Controller.registerEvent(
                        // Oh, IDK, 2 to 3 minutes...
                        millisFromNow = TimeUnit.SECONDS.toMillis(120 + Random.nextLong(60)),
                        payload = DeviceOnline(device = this),
                        listener = scheduler
                    )
                }
                else -> { // The device will do the work, but will never deliver the work completed message
                    // Do nothing
                }
            }
        }
        payload.statusRequest()?.let { _ -> // If the device is alive, let the scheduler know whether you're working
            if (alive) {
                Controller.registerEvent(
                    payload = StatusUpdate(
                        device = this,
                        status = Status(isWorking())
                    ),
                    listener = scheduler
                )
            }
        }
        payload.sleep()?.let { _ -> // Internal event to put the device to sleep in a controlled manner
            alive = false
            workScheduledUntil = -1
            Controller.registerEvent(
                payload = DeviceOffline(this),
                listener = scheduler
            )
        }
        payload.awake()?.let { _ -> // Internal event to wake up the device
            alive = true
            Controller.registerEvent(
                payload = DeviceOnline(this),
                listener = scheduler
            )
        }
    }

    /**
     * @return whether the device is working.
     */
    private fun isWorking() = workScheduledUntil < Controller.currentTimeMillis()

    override fun equals(other: Any?) = other is Device && id == other.id
    override fun hashCode() = id
    override fun toString() = name


    companion object {
        private var deviceCount = 0
    }
}
