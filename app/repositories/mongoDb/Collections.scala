package repositories.mongoDb

sealed abstract class MongoCollection(val name: String)
case object TechCollection extends MongoCollection("tech")
case object TechVoteLogCollection extends MongoCollection("techVoteLog")
case object CompCollection extends MongoCollection("comp")
