package models.ui

case class MainModel(cities: Seq[City],
                     techs: Seq[Tech],
                     city: Option[String],
                     tech: Option[String])
