import controllers._
import controllers.auth.{AuthTechControllerImpl, AuthTechController, AuthCompControllerImpl, AuthCompController}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import repositories._
import repositories.mongoDb._
import router.Routes
import services.{LocationService, AuthService, CompService, TechService}
import services.impl.{LocationServiceImpl, AuthServiceImpl, CompServiceImpl, TechServiceImpl}

class HrstkaApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = new HrstkaComponents(context).application
}

class HrstkaComponents(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {
  // Injected router
  lazy val router: Router = new Routes(
    httpErrorHandler,
    compController,
    appController,
    authController,
    authTechController,
    authCompController,
    apiController,
    assets
  )

  // Controllers
  lazy val appController = new AppControllerImpl(locationService, techService, messagesApi)
  lazy val compController: CompController = new CompControllerImpl(compService, authService, techService, locationService, messagesApi)
  lazy val authController = new AuthControllerImpl(authService, locationService, techService, messagesApi)
  lazy val apiController: ApiController = new ApiControllerImpl(compService, techService, locationService)

  // Controllers that require authorization
  lazy val authCompController: AuthCompController = new AuthCompControllerImpl(compService, authService, techService, locationService, messagesApi)
  lazy val authTechController: AuthTechController = new AuthTechControllerImpl(authService, locationService, techService, messagesApi)

  lazy val assets = new controllers.Assets(httpErrorHandler)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteRepository, techVoteLogRepository)
  lazy val compService: CompService = new CompServiceImpl(compRepository, techService, locationService)
  lazy val authService: AuthService = new AuthServiceImpl(userRepository)
  lazy val locationService: LocationService = new LocationServiceImpl(cityRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteRepository: VoteRepository = MongoTechVoteRepository
  lazy val techVoteLogRepository: VoteLogRepository = MongoTechVoteLogRepository
  lazy val compRepository: CompRepository = new MongoCompRepository
  lazy val userRepository: UserRepository = new MongoUserRepository
  lazy val cityRepository: CityRepository = new MongoCityRepository
}