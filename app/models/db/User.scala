package models.db

case class User(_id: Identifiable.Id,
                email: String,
                encryptedPassword: String,
                role: String) extends Identifiable