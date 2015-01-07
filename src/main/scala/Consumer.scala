/**
 * Created by anthony on 1/5/15.
 */

package com.ntp.anthonyc

import akka.actor._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class Consumer(private val producer: ActorRef, private var numOfKeepAliveLeft: Int) extends Actor {

  private var isRunning = false

  def receive: Actor.Receive = {
    case Time(time) =>
      println(time + " received in " +
      context.actorOf(Props(this)) +
      ", keep alive messages left = " + numOfKeepAliveLeft)

    case Start => start()

    // Internal message by the scheduler to instruct consumer to send keep alive
    case SendKeepAlive => sendKeepAlive()
  }

  private def start(): Unit = {
    if (!isRunning) {
      producer ! Register
      sendFutureKeepAlive()
      isRunning = true
    }
  }

  private def sendKeepAlive(): Unit = {
    if (numOfKeepAliveLeft > 0) {
      numOfKeepAliveLeft -= 1
      producer ! KeepAlive
      sendFutureKeepAlive()
    }
  }

  private def sendFutureKeepAlive(): Unit = {
    // This will cause the consumer to send a KeepAlive to the producer
    context.system.scheduler.scheduleOnce(
      Duration(5, "seconds"),
      self,
      SendKeepAlive
    )
  }
}
