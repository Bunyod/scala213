package com.bunic.scala213

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.bunic.scala213.domain.template
import com.bunic.scala213.repository.template.inmemmory
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{ExecutionContext, Future}

object Boot
    extends App
    with RouteConcatenation
    with util.cfg.Configurable
    with util.http.ExceptionHandler
    with util.http.RejectionHandler
    with LazyLogging {

  def startApplication(): Future[Http.ServerBinding] = {

    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()(actorSystem)

    val repoTweet: inmemmory.HelloRepository = new inmemmory.HelloRepository()
    val svcTweet: template.HelloService = new template.HelloService(repoTweet)
    val rteTweet: http.template.HelloRoutes = new http.template.HelloRoutes(svcTweet)
    val tweets: Route = rteTweet.route

    val routes: Route = handleRejections(rejectionHandler) {
      handleExceptions(exceptionHandler) {
        cors() {
          tweets
        }
      }
    }

    Http().bindAndHandle(routes, config.http.host, config.http.port)
  }

  startApplication()

}
