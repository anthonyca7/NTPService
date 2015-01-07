/**
 * Created by anthony on 1/5/15.
 */

package com.ntp.anthonyc

import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Date
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

case class Client(consumer: ActorRef, var lastKeepAlive: Long)

class Producer() extends Actor with ActorLogging {
  private var clients = new ArrayBuffer[Client]()
  private var isRunning = false

  def receive: Actor.Receive = {

    case KeepAlive =>
      // sets lastKeepAlive for the sender if is in consumers otherwise we just ignore the request
      operateOnConsumer(sender()) {
        _.lastKeepAlive = System.currentTimeMillis
      }

    case Register =>
      val consumer = sender()
      context watch consumer
      clients += Client(consumer, System.currentTimeMillis)


    case Start => start()

    // These two internal messages are used by the scheduler
    case Update => updateConsumers()
    case Broadcast => broadcastTime()
  }

  private def start(): Unit = {
    if (!isRunning) {
      makeFutureBroadcast()
      makeFutureUpdate()
      isRunning = true
    }
  }

  private def updateConsumers(): Unit = {

    def removeIdleConsumers(client: Client): Unit = {
      if (System.currentTimeMillis - client.lastKeepAlive > 10000) {
        clients -= client

        println("removed the worker at " + client.consumer)
        println("\t" + clients.length + " clients left")
      }
    }

    for (i <- clients.length - 1 to 0 by -1) {
      val client = clients(i)
      operateOnConsumer(client.consumer)(removeIdleConsumers)
    }

    if (clients.length == 0 && isRunning) {
      println("All workers ended and the system is going to shutdown")
      context.stop(self)
      context.system.shutdown()
      isRunning = false
    }
    else {
      makeFutureUpdate()
    }
  }

  private def broadcastTime(): Unit = {
    println("BROADCASTING TIME FROM PRODUCER")
    val currentTime = new Date()
    val time = Time(currentTime.toString)

    // Send a Time message to each registered consumer
    clients foreach (_.consumer ! time)

    makeFutureBroadcast()
  }

  private def operateOnConsumer(consumer: ActorRef)(success: (Client) => Unit): Unit = {
    val searchResult = clients find (_.consumer == consumer)

    searchResult match {
      case Some(client) => success(client)
      case None => // Ignores requests from consumers that are not registered
    }
  }

  private def makeFutureUpdate(): Unit = {
    context.system.scheduler.scheduleOnce(
      Duration(100, "milliseconds"),
      self,
      Update
    )
  }

  private def makeFutureBroadcast(): Unit = {
    context.system.scheduler.scheduleOnce(
      Duration(1, "seconds"),
      self,
      Broadcast
    )
  }
}


