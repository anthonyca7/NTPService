package com.ntp.anthonyc

import akka.actor._
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
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

    for (i <- 0 until numOfConsumers) {
      val consumer = system.actorOf(Props(new Consumer(producer, random.nextInt(13))))
      consumer ! Start
    }

    producer ! Start
  }
}
