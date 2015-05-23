package models.ui

import models.domain.Role

case class MainModel(cities: Seq[City],
                     techs: Seq[Tech],
                     city: Option[String],
                     tech: Option[String],
                     role: Option[Role])
