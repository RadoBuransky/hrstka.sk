package sk.hrstka.models.db

import sk.hrstka.models.db.Identifiable.{Handle, Id}

/**
 * City.
 *
 * @param _id Identifier.
 * @param handle Human-friendly identifier.
 * @param en English name.
 * @param countryCode ISO 3166 country code.
 */
case class City(_id: Id,
                handle: Handle,
                en: String,
                countryCode: String) extends Identifiable
