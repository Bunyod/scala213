package com.bunic.scala213.domain.template

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

final case class Tweet(text: String) {

  def transform: String =
    text.trim.toUpperCase

}

object Tweet {

  implicit val decoder: Decoder[Tweet] = deriveDecoder
  implicit val encoder: Encoder[Tweet] = deriveEncoder

}
