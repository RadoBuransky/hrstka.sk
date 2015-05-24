package models.ui

import models.domain.User

case class MainModel(cities: Seq[City],
                     techs: Seq[Tech],
                     city: Option[String],
                     tech: Option[String],
                     user: Option[User])
