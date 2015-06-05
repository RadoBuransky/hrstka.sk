package sk.hrstka.models.db

import reactivemongo.bson.BSONObjectID

object TechVoteSpec {
  val scalaRado = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.scala._id,
    userId  = UserSpec.rado._id,
    value   = 3
  )

  val javaRado = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.java._id,
    userId  = UserSpec.rado._id,
    value   = 3
  )

  val javaJohny = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.java._id,
    userId  = UserSpec.johny._id,
    value   = -1
  )

  val phpRado = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.php._id,
    userId  = UserSpec.rado._id,
    value   = -1
  )

  val akkaRado = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.akka._id,
    userId  = UserSpec.rado._id,
    value   = 2
  )

  val akkaJohny = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.akka._id,
    userId  = UserSpec.johny._id,
    value   = 3
  )

  val apacheRado = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.apache._id,
    userId  = UserSpec.rado._id,
    value   = -1
  )

  val apacheJohny = TechVote(
    _id     = BSONObjectID.generate,
    techId  = TechSpec.apache._id,
    userId  = UserSpec.johny._id,
    value   = 2
  )

  lazy val all = Iterable(scalaRado, javaRado, javaJohny, phpRado, akkaRado, akkaJohny, apacheRado, apacheJohny)
}
