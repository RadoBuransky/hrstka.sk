package models.domain

/**
 * Technology categorization.
 */
sealed trait TechCategory {
  /**
   * Human-friendly identifier.
   */
  def handle: Handle = HandleFactory.fromHumanName(en)

  /**
   * Slovak name.
   */
  def en: String
}

/**
 * Java, .NET, ARM, x86, Linux, Windows, ...
 */
object Platform extends TechCategory {
  override val en = "Platform"
}

/**
 * Spring, ASP.NET, Play Framework, Rails, ...
 */
object Framework extends TechCategory {
  override val en = "Framework"
}

/**
 * Akka, SLF4J, Joda time, ...
 */
object Library extends TechCategory {
  override val en = "Library"
}

/**
 * Scala, Java, C#, PHP, ...
 */
object Language extends TechCategory {
  override val en = "Language"
}

/**
 * Oracle, MS SQL, MongoDB, Redis, FileNet, ...
 */
object Database extends TechCategory {
  override val en = "Database"
}

/**
 * Apache, Netty, JBoss, WebSphere, IIS, ...
 */
object Server extends TechCategory {
  override val en = "Server"
}

/**
 * Jenkins, Gradle, Sonar, GitLab, Artifactory, Subversion, ...
 */
object Tool extends TechCategory {
  override val en = "Tool"
}

/**
 * Scrum, Kanban, RUP, ITIL, ...
 */
object Methodology extends TechCategory {
  override val en = "Methodology"
}

/**
 * Aviation, Insurance, Internet, Game, ...
 */
object Domain extends TechCategory {
  override val en = "Domain"
}

/**
 * If nothing else fits...
 */
object Other extends TechCategory {
  override val en = "Other"
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