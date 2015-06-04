package sk.hrstka.common

import org.slf4j.LoggerFactory

/**
 * Common mix-in trait for logging.
 */
trait Logging {
  protected val logger = LoggerFactory.getLogger(this.getClass)
}
