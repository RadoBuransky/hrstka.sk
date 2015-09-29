package sk.hrstka.models.ui

import play.api.Mode.Mode
import sk.hrstka.models.domain.User

case class MainModel(cities: Seq[City],
                     techRatings: Seq[TechRating],
                     user: Option[User],
                     mode: Mode,
                     title: String = MainModelSingleton.defaultTitle,
                     description: String = MainModelSingleton.defaultDescription,
                     searchQuery: String = "")

object MainModelSingleton {
  val defaultTitle = "Hŕstka"
  val defaultDescription = "PROGRAMMER'S DELICATESSEN"
}
