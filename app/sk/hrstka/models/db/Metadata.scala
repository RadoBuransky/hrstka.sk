package sk.hrstka.models.db

/**
 * Database metadata.
 *
 * @param _id Identifier.
 * @param dbVersion Database version.
 */
case class Metadata(_id: Identifiable.Id,
                    dbVersion: Int) extends Identifiable
