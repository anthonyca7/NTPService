package com.ntp.anthonyc

sealed trait Message

case object Register extends Message
case object KeepAlive extends Message
case object Broadcast extends Message
case object Update extends Message
case object Start extends Message
case object SendKeepAlive extends Message
case class Time(timestamp: String) extends Message

