/**
 * Created by anthony on 1/5/15.
 */

package com.ntp.anthonyc

import akka.actor._

class Consumer(private val producer: ActorRef, private var requestLimit: Int) extends Actor {
  def start(): Unit = {
    producer ! Register
  }

  def receive: Actor.Receive = {
    case Time(time) => println(time + " received in " + context.actorOf(Props(this)).path + " k = " + requestLimit)
    case Start => start()

    // Used by the scheduler to instruct consumer to send keep alive
    case Keep => sendKeepAlive()
  }

  def sendKeepAlive(): Unit = {
    requestLimit -= 1

    if (requestLimit > 0) {
      producer ! KeepAlive
    }
  }
}
