import controllers.{AppController, TechController}

package object AppLoader {
  lazy val appController = AppController()
  lazy val techController = TechController()
}
