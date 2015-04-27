package models.db

case class User(id: Identifiable.Id,
                email: String,
                encryptedPassword: String,
                role: String)
