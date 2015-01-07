/**
 * Created by anthony on 1/7/15.
 */

package com.ntp.anthonyc

import akka.actor._
import akka.testkit._
import com.typesafe.config.ConfigFactory
import org.scalatest._
import scala.concurrent.duration._

class TimeBroadcastTest

  extends TestKit(ActorSystem("testsystem",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val numOfConsumers = 2
  val producerRef = TestActorRef(new Producer())
  val consumerRef1 = TestActorRef(new Consumer(producerRef, 0))
  val consumerRef2 = TestActorRef(new Consumer(producerRef, 2))

  "The system should" should {
    "producer prints broadcasting info" in {
      consumerRef1 ! Start
      consumerRef2 ! Start
      producerRef ! Start

      within(15 seconds) {
        EventFilter.info(message = "BROADCASTING TIME FROM PRODUCER", occurrences = 14) intercept {
          expectNoMsg()
        }
      }

      within(5 seconds) {
        EventFilter.info(message = "BROADCASTING TIME FROM PRODUCER", occurrences = 5) intercept {
          expectNoMsg()
        }
      }
    }
  }

  override def afterAll {
    shutdown()
  }
}


