package repositories.mongoDb

sealed abstract class MongoCollection(val name: String)
case object TechCollection extends MongoCollection("tech")
case object TechVoteCollection extends MongoCollection("techVote")
case object TechVoteLogCollection extends MongoCollection("techVoteLog")
case object CompCollection extends MongoCollection("comp")
case object CompTechCollection extends MongoCollection("compTech")
case object CompVoteCollection extends MongoCollection("compVote")
case object CompVoteLogCollection extends MongoCollection("compVoteLog")
