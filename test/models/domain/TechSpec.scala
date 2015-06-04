package models.domain

import models.db

object TechSpec {
  val scalaRating = TechRating(TechFactory(db.TechSpec.scala), 1.0)
  val javaRating = TechRating(TechFactory(db.TechSpec.java), 0.5)
  val phpRating = TechRating(TechFactory(db.TechSpec.php), 0.0)
  val akkaRating = TechRating(TechFactory(db.TechSpec.akka), 0.9)
  val apacheRating = TechRating(TechFactory(db.TechSpec.apache), 0.6)
  lazy val allRatings = Seq(scalaRating, javaRating, phpRating, akkaRating, apacheRating).sortBy(-1 * _.value)
  def forHandle(handle: String): TechRating = allRatings.find(_.tech.handle.value == handle).get
}