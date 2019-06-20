package com.bunic.scala213
package util
package cfg

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pureconfig._

trait Configurable {
  val config: Config = Configurable.config
}

object Configurable extends AnyRef with LazyLogging {
  import pureconfig.generic.auto._

  val dlConfig: com.typesafe.config.Config = ConfigFactory.load()
  val config: Config = loadConfigOrThrow[Config](dlConfig)
}
