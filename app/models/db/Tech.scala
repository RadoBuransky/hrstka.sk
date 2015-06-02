package models.db

import models.db.Identifiable.{Handle, Id}

/**
 * A technology
 *
 * @param _id Identifier.
 * @param handle Human-friendly identifier.
 * @param categoryHandle Language, framework, tool, methodology, ...
 * @param name Nice English name.
 * @param website URL with more info.
 */
case class Tech(_id: Id,
                handle: Handle,
                categoryHandle: String,
                name: String,
                website: String) extends Identifiable