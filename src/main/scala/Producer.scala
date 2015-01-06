/**
 * Created by anthony on 1/5/15.
 */

package com.ntp.anthonyc

import akka.actor._
import akka.routing._
import scala.collection.mutable.ArrayBuffer

case class Client(consumer: ActorRef, var lastKeepAlive: Long)

class Producer() extends Actor {
  private var clients = new ArrayBuffer[Client]()
  val router = Router(RoundRobinRoutingLogic(), Vector[ActorRefRoutee]())


  def receive: Actor.Receive = {

    case KeepAlive =>
      // sets lastKeepAlive for the sender if is in consumers otherwise we just ignore the request
      operateOnConsumer(sender()) {
        _.lastKeepAlive = System.currentTimeMillis
      }

    case Register =>
      val consumer = sender()
      context watch consumer
      router.addRoutee(consumer)
      clients += Client(consumer, System.currentTimeMillis)

    // These two messages are used by the scheduler
    case Update => updateConsumers()
    case Broadcast => broadcastTime()
  }


  def updateConsumers(): Unit = {
    def removeIdleConsumers(client: Client): Unit = {
      if (System.currentTimeMillis - client.lastKeepAlive > 10000) {
//        client.consumer.path.address
        println("removed the worker at " + client.consumer.path)
        router.removeRoutee(client.consumer)
        clients -= client
        println("\t" + clients.length + " clients left")
      }
    }

    for (i <- clients.length - 1 to 0 by -1) {
      val client = clients(i)
      operateOnConsumer(client.consumer)(removeIdleConsumers)
    }
  }

  def broadcastTime(): Unit = {
    println("BROADCASTING TIME FROM PRODUCER")
    val time = Time(System.currentTimeMillis)
    // Send a Time message to each registered consumer
    clients foreach (_.consumer ! time)
  }

  def operateOnConsumer(consumer: ActorRef)(success: (Client) => Unit): Unit = {
    val searchResult = clients find (_.consumer == consumer)

    searchResult match {
      case Some(client) => success(client)
      case None =>
    }
  }
}


