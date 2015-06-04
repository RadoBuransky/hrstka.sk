package sk.hrstka.models.domain

import sk.hrstka.models

object TechSpec {
  val scalaRating = TechRating(TechFactory(models.db.TechSpec.scala), 1.0)
  val javaRating = TechRating(TechFactory(models.db.TechSpec.java), 0.5)
  val phpRating = TechRating(TechFactory(models.db.TechSpec.php), 0.0)
  val akkaRating = TechRating(TechFactory(models.db.TechSpec.akka), 0.9)
  val apacheRating = TechRating(TechFactory(models.db.TechSpec.apache), 0.6)
  lazy val allRatings = Seq(scalaRating, javaRating, phpRating, akkaRating, apacheRating).sortBy(-1 * _.value)
  def forHandle(handle: String): TechRating = allRatings.find(_.tech.handle.value == handle).get
}