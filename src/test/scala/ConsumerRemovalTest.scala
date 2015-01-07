///**
// * Created by anthony on 1/7/15.
// */
//
package com.ntp.anthonyc

import akka.actor._
import akka.testkit._
import com.typesafe.config.ConfigFactory
import org.scalatest._
import scala.concurrent.duration._

class ConsumerRemovalTest
  extends TestKit(ActorSystem("testsystem",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val numOfConsumers = 3
  val producerRef = TestActorRef(new Producer())
  val consumerRef1 = TestActorRef(new Consumer(producerRef, 0))
  val consumerRef2 = TestActorRef(new Consumer(producerRef, 1))
  val consumerRef3 = TestActorRef(new Consumer(producerRef, 2))
  val consumerRef4 = TestActorRef(new Consumer(producerRef, 3))

  "The system should" should {
    "producer prints broadcasting info" in {

      consumerRef1 ! Start
      consumerRef2 ! Start
      consumerRef3 ! Start
      consumerRef4 ! Start
      producerRef ! Start

      within(11 seconds) {
        EventFilter.info(pattern = "removed the worker at TestActor.*", occurrences = 1) intercept {
          expectNoMsg()
        }
      }

      // The other three messages should end afterwards
      within(15 seconds) {
        EventFilter.info(pattern = "removed the worker at TestActor.*", occurrences = 3) intercept {
          expectNoMsg()
        }
      }
    }
  }

  override def afterAll {
    shutdown()
  }
}