/**
 * Created by anthony on 1/5/15.
 */

package com.ntp.anthonyc


import java.util.Date

import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration

case class Client(consumer: ActorRef, var lastKeepAlive: Long)

class Producer() extends Actor {
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

    // These two messages are used by the scheduler
    case Update => updateConsumers()
    case Broadcast => broadcastTime()
  }

  def start(): Unit = {
    if (!isRunning) {
      makeFutureBroadcast()
      makeFutureUpdate()
      isRunning = true
    }
  }


  def updateConsumers(): Unit = {
    def removeIdleConsumers(client: Client): Unit = {
      if (System.currentTimeMillis - client.lastKeepAlive > 10000) {
        println("removed the worker at " + client.consumer)
        clients -= client
        println("\t" + clients.length + " clients left")
      }
    }

    for (i <- clients.length - 1 to 0 by -1) {
      val client = clients(i)
      operateOnConsumer(client.consumer)(removeIdleConsumers)
    }

    if (clients.length == 0 && isRunning) {
      println("All workers ended and the system is going to shutdown")
      context.system.shutdown()
      isRunning = false
    }

    makeFutureUpdate()
  }

  def broadcastTime(): Unit = {
    println("BROADCASTING TIME FROM PRODUCER")
    val currentTime = new Date()
    val time = Time(currentTime.toString)

    // Send a Time message to each registered consumer
    clients foreach (_.consumer ! time)

    makeFutureBroadcast()
  }

  def operateOnConsumer(consumer: ActorRef)(success: (Client) => Unit): Unit = {
    val searchResult = clients find (_.consumer == consumer)

    searchResult match {
      case Some(client) => success(client)
      case None =>
    }
  }

  def makeFutureUpdate(): Unit = {
    context.system.scheduler.scheduleOnce(
      Duration(100, "milliseconds"),
      self,
      Update
    )
  }

  def makeFutureBroadcast(): Unit = {
    context.system.scheduler.scheduleOnce(
      Duration(1, "seconds"),
      self,
      Broadcast
    )
  }
}


