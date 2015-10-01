package sk.hrstka.views.helpers

import play.twirl.api.Html
import sk.hrstka.models.ui.{TechRating, City}

object Formatting {
  def govBizToString(govBiz: Option[BigDecimal]): String = govBiz.map("%1.0f%%".format(_)).getOrElse("")

  def techRatingsToHtml(techRatings: Seq[TechRating]): Html = {
    val sb = new StringBuilder
    sb.append("""<ol class="hrstka-techs">""")
    techRatings.foreach { techRating =>
      val url = sk.hrstka.controllers.routes.CompController.cityTech(sk.hrstka.controllers.impl.CompControllerImpl.anywhere, techRating.tech.handle)
      sb.append(s"""<li><a href="$url"><span class="hrstka-tech">${techRating.tech.name}</span></a></li>""")
    }
    sb.append("""</ol>""")
    Html(sb.toString())
  }

  def citiesToHtml(cities: Seq[City]): Html = {
    val sb = new StringBuilder

    sb.append("""<div class="cities">""")
    val lastCity = cities.last
    cities.foreach { city =>
      val separator = if (city == lastCity) "" else ", "
      val url = sk.hrstka.controllers.routes.CompController.cityTech(city.handle, "")

      sb.append(s"""<a href="$url">""")
      sb.append(s"""<span class="hrstka-city">${city.en}</span>""")
      sb.append(s"""</a>$separator""")
    }
    sb.append("""</div>""")

    Html(sb.toString())
  }
}
