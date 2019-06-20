package com.bunic.scala213.domain.template

import scala.concurrent.Future

trait HelloRepositoryAlgebra {
  def searchByUserName(username: String, limit: Int): Future[Seq[Tweet]]
}
