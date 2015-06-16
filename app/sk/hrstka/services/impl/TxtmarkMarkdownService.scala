package sk.hrstka.services.impl

import com.github.rjeschke.txtmark.Processor
import com.google.inject.Singleton
import sk.hrstka.services.MarkdownService;

@Singleton
final class TxtmarkMarkdownService extends MarkdownService {
  override def toHtml(markdown: String): String = Processor.process(markdown)
}
