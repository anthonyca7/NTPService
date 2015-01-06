package com.ntp.anthonyc

import akka.actor._
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object NTPService {

  def main(args: Array[String]): Unit = {

    if (args.length < 1 || !args(0).matches("\\d+") || args(0).toInt == 0) {
      println("Enter a valid integer greater than 1 as the program argument")
      System.exit(1)
    }

    val numOfConsumers = args(0).toInt
    val system = ActorSystem("NTPServiceSystem")
    val random = new Random(System.currentTimeMillis())

    val producer = system.actorOf(Props(new Producer()))

    var consumers = new ArrayBuffer[ActorRef]()
    for (i <- 0 until numOfConsumers) {
      val consumer = system.actorOf(Props(new Consumer(producer, random.nextInt(13))))
      consumers += consumer
      consumer ! Start
    }

    producer ! Update

    system.scheduler.schedule(
      Duration(0, "milliseconds"),
      Duration(100, "milliseconds"),
      producer,
      Update
    )

    system.scheduler.schedule(
      Duration(0, "seconds"),
      Duration(5, "seconds"),
      new Runnable {
        override def run(): Unit = {
          consumers foreach (_ ! Keep)
        }
      }
    )
    system.scheduler.schedule(
        Duration(0, "seconds"),
        Duration(1, "seconds"),
        producer,
        Broadcast
    )


  }
}
