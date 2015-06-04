package sk.hrstka.models.db

import java.net.URL

import reactivemongo.bson.BSONObjectID

object CompSpec {
  lazy val avitech = Comp(
    _id               = BSONObjectID.generate,
    authorId          = BSONObjectID.generate,
    name              = "Avitech",
    website           = new URL("http://avitech.aero/").toString,
    city              = CitySpec.bratislava.handle,
    employeeCount     = Some(60),
    codersCount       = Some(30),
    femaleCodersCount = Some(5),
    note              = "note",
    products          = true,
    services          = true,
    internal          = false,
    techs             = Set(TechSpec.scala.handle, TechSpec.java.handle),
    joel              = Set(3, 5, 7)
  )

  lazy val borci = Comp(
    _id               = BSONObjectID.generate,
    authorId          = BSONObjectID.generate,
    name              = "Borci",
    website           = new URL("http://borci.sk/").toString,
    city              = CitySpec.noveZamky.handle,
    employeeCount     = Some(23),
    codersCount       = Some(23),
    femaleCodersCount = Some(5),
    note              = "",
    products          = true,
    services          = false,
    internal          = false,
    techs             = Set(TechSpec.php.handle, TechSpec.apache.handle),
    joel              = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
  )
}
