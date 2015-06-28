package sk.hrstka.models.db

import reactivemongo.bson.BSONObjectID

object CompVoteSpec {
  val avitechRado = CompVote(
    _id       = BSONObjectID.generate,
    entityId  = CompSpec.avitech._id,
    userId    = UserSpec.rado._id,
    value     = 3)

  val avitechJohny = CompVote(
    _id       = BSONObjectID.generate,
    entityId  = CompSpec.avitech._id,
    userId    = UserSpec.johny._id,
    value     = -1)

  val borciJohny = CompVote(
    _id       = BSONObjectID.generate,
    entityId  = CompSpec.borci._id,
    userId    = UserSpec.johny._id,
    value     = 2)

  val all = Iterable(avitechRado, avitechJohny, borciJohny)
}
