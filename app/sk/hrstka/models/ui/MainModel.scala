package sk.hrstka.models.ui

import play.api.Mode.Mode
import sk.hrstka.models.domain.User

case class MainModel(cities: Seq[City],
                     techs: Seq[Tech],
                     city: Option[String],
                     tech: Option[String],
                     user: Option[User],
                     mode: Mode)
