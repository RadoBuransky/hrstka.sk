package models.domain

/**
 * Technology categorization.
 */
sealed trait TechCategory {
  /**
   * Human-friendly identifier.
   */
  def handle: Handle = Handle.fromHumanName(sk)

  /**
   * Slovak name.
   */
  def sk: String
}

/**
 * Java, .NET, ARM, x86, Linux, Windows, ...
 */
object Platform extends TechCategory {
  override val sk = "Platforma"
}

/**
 * Spring, ASP.NET, Play Framework, Rails, ...
 */
object Framework extends TechCategory {
  override val sk = "Framework"
}

/**
 * Akka, SLF4J, Joda time, ...
 */
object Library extends TechCategory {
  override val sk = "Knižnica"
}

/**
 * Scala, Java, C#, PHP, ...
 */
object Language extends TechCategory {
  override val sk = "Programovací jazyk"
}

/**
 * Oracle, MS SQL, MongoDB, Redis, FileNet, ...
 */
object Database extends TechCategory {
  override val sk = "Databáza"
}

/**
 * Apache, Netty, JBoss, WebSphere, IIS, ...
 */
object Server extends TechCategory {
  override val sk = "Server"
}

/**
 * Jenkins, Gradle, Sonar, GitLab, Artifactory, Subversion, ...
 */
object Tool extends TechCategory {
  override val sk = "Nástroj"
}

/**
 * Scrum, Kanban, RUP, ITIL, ...
 */
object Methodology extends TechCategory {
  override val sk = "Metodológia"
}

/**
 * Aviation, Insurance, Internet, Game, ...
 */
object Domain extends TechCategory {
  override val sk = "Doména"
}

/**
 * If nothing else fits...
 */
object Other extends TechCategory {
  override val sk = "Ostatné"
}

object TechCategory {
  val allCategories = Seq(
    Platform,
    Framework,
    Library,
    Language,
    Database,
    Server,
    Tool,
    Methodology,
    Domain,
    Other
  )

  def apply(categoryHandle: String): TechCategory = allCategories.find(_.handle.value == categoryHandle).getOrElse(Other)
}