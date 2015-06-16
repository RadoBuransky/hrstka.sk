package sk.hrstka.services.impl

import sk.hrstka.test.BaseSpec

class TxtmarkMarkdownServiceSpec extends BaseSpec {
  behavior of "toHtml"

  it should "convert H1" in new TestScope {
    test("# heading 1", "<h1>heading 1</h1>")
  }

  it should "convert H2" in new TestScope {
    test("## heading 2", "<h2>heading 2</h2>")
  }

  it should "convert H3" in new TestScope {
    test("### heading 3", "<h3>heading 3</h3>")
  }

  it should "convert H4" in new TestScope {
    test("#### heading 4", "<h4>heading 4</h4>")
  }

  it should "convert simple text" in new TestScope {
    test("a", "<p>a</p>")
  }

  it should "convert paragraps" in new TestScope {
    test("a\r\n\r\nb", "<p>a</p>\n<p>b</p>")
  }

  it should "convert strong" in new TestScope {
    test("x **a** y", "<p>x <strong>a</strong> y</p>")
  }

  it should "convert emphasis" in new TestScope {
    test("*a*", "<p><em>a</em></p>")
  }

  it should "convert unordered lists" in new TestScope {
    test(
      """* a
        |* b
      """.stripMargin,
      """<ul>
        |<li>a</li>
        |<li>b</li>
        |</ul>""".stripMargin)
  }

  it should "convert ordered lists" in new TestScope {
    test(
      """1. a
        |2. b
      """.stripMargin,
      """<ol>
        |<li>a</li>
        |<li>b</li>
        |</ol>""".stripMargin)
  }

  private class TestScope {
    lazy val service = new TxtmarkMarkdownService()

    protected def test(markdown: String, expectedHtml: String) =
      assert(service.toHtml(markdown) == expectedHtml + "\n")
  }
}
