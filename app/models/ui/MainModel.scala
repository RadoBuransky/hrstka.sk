package models.ui

import models.domain.User
import play.api.Mode.Mode

case class MainModel(cities: Seq[City],
                     techs: Seq[Tech],
                     city: Option[String],
                     tech: Option[String],
                     user: Option[User],
                     mode: Mode)
