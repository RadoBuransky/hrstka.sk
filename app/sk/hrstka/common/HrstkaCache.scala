package sk.hrstka.common

/**
 * Shared Hrstka cache interface.
 */
trait HrstkaCache {
  /**
   * Invalidates everything.
   */
  def invalidate(): Unit
}
