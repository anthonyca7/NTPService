package com.ntp.anthonyc

sealed trait ProducerMessage
sealed trait ConsumerMessage
sealed trait OperationMessage

case object Register extends ConsumerMessage
case object KeepAlive extends ConsumerMessage

case class Time(timestamp: String) extends ProducerMessage

case object Broadcast extends OperationMessage
case object Update extends OperationMessage
case object Start extends OperationMessage
case object SendKeepAlive extends OperationMessage

