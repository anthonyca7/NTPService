/**
 * Created by anthony on 1/7/15.
 */

package com.ntp.anthonyc

import akka.actor._
import akka.testkit.EventFilter
import com.typesafe.config.ConfigFactory

class ServiceTest() {
  implicit val system = ActorSystem("testsystem", ConfigFactory.parseString(
   """
    akka.loggers = ["akka.testkit.TestEventListener"]
   """))

  try {
    val producer = system.actorOf(Props[Producer])
    EventFilter[ActorKilledException](occurrences = 1) intercept {
      producer ! Start
    }

  } finally {
    system.shutdown()
  }
}


