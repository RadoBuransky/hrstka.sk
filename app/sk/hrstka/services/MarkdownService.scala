package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.services.impl.TxtmarkMarkdownService

/**
 * Service for handling Markdown format.
 * http://daringfireball.net/projects/markdown/syntax/
 */
@ImplementedBy(classOf[TxtmarkMarkdownService])
trait MarkdownService {
  /**
   * Converts markdown to HTML.
   *
   * @param markdown Markdown text source.
   * @return HTML.
   */
  def toHtml(markdown: String): String
}
