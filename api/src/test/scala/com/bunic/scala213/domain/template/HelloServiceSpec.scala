package com.bunic.scala213.domain.template

import akka.actor.ActorSystem
import com.bunic.scala213.repository.template.inmemmory.HelloRepository
import com.bunic.scala213.UnitSpec

import scala.concurrent.Future

class HelloServiceSpec extends UnitSpec with StubData {

  val actsys: ActorSystem = system

  val trim: HelloRepository = stub[HelloRepository]
  val svc: HelloService = new HelloService(trim)

  "The Shout service" when {
    "initiated for getting last N tweets" should {
      "response with a 'Seq[Tweet]'" in {

        val limit = 3
        val trimMock: Future[Seq[Tweet]] = Future(tweets(limit))
        (trim.searchByUserName _).when(userName, limit).returns(trimMock)

        whenReady(svc.tweets(userName, limit).value) {
          case Right(res) => res.size shouldEqual limit
          case Left(err)  => failTest(err.message)
        }
      }

      "response with 10 tweets" in {
        val limit = 10
        val trimMock: Future[Seq[Tweet]] = Future(tweets(limit))
        (trim.searchByUserName _).when(userName, limit).returns(trimMock)

        svc.tweets(userName, limit).value.futureValue match {
          case Right(res) => res.size shouldEqual limit
          case Left(err)  => failTest(err.message)
        }
      }

      "response with an error stating the api is not available" in {
        val limit = 10
        val trimMock: Future[Seq[Tweet]] = Future.failed(new Exception("Wrong credentials..."))
        (trim.searchByUserName _).when(userName, limit).returns(trimMock)

        svc.tweets(userName, limit).value.futureValue match {
          case Left(err) => err.message shouldEqual "Twitter api is not available"
          case Right(_)  => failTest("Twitter connection error expected")
        }
      }

    }
  }

}
