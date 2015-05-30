import play.api.ApplicationLoader
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}

class HrstkaApplicationLoader extends GuiceApplicationLoader() {
  override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    super.builder(context)
  }
}
/*
final class HrstkaComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with I18nComponents
  with ReactiveMongoComponents
  with EhCacheComponents {

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
  lazy val techRepository: TechRepository = new MongoTechRepository(reactiveMongoApi)
  lazy val techVoteRepository: VoteRepository = new MongoTechVoteRepository(reactiveMongoApi)
  lazy val techVoteLogRepository: VoteLogRepository = new MongoTechVoteLogRepository(reactiveMongoApi)
  lazy val compRepository: CompRepository = new MongoCompRepository(reactiveMongoApi)
  lazy val userRepository: UserRepository = new MongoUserRepository(reactiveMongoApi)
  lazy val cityRepository: CityRepository = new MongoCityRepository(reactiveMongoApi)
}*/