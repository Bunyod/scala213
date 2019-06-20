package com.bunic.scala213
package http
package template

import akka.http.scaladsl.server
import akka.http.scaladsl.server.{Directives, RouteConcatenation}
import cats.implicits._
import com.bunic.scala213.domain.template.{HelloService, Tweet}
import com.bunic.scala213.util.cfg.Configurable
import com.bunic.scala213.util.json.FailFastCirceSupport
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class HelloRoutes(svc: HelloService)(implicit ec: ExecutionContext)
    extends Directives
    with Configurable
    with FailFastCirceSupport
    with RouteConcatenation
    with LazyLogging {

  import akka.http.scaladsl.server.{Directives => directive}

  val route = getTweets ~ add

  def getTweets: server.Route = directive.get {
    path("tweet" / Segment) { username =>
      parameters('limit.as[Int]) { limit =>
        complete(
          svc.tweets(username, limit).fold(err => err.toHttpResponse, res => res.asJson.toHttpResponse)
        )
      }
    }
  }

  def add: server.Route = directive.post {
    path("tweet") {
      entity(as[Tweet]) { twt =>
        complete(
          svc
            .tweets(twt.text, 12)
            .fold(err => err.toHttpResponse, res => res.asJson.toHttpResponse)
        )
      }
    }
  }

}
