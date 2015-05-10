import controllers._
import repositories.mongoDb._
import repositories._
import services.impl.{LocationServiceImpl, AuthServiceImpl, CompServiceImpl, TechServiceImpl}
import services.{LocationService, AuthService, CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)
  lazy val compController: CompController = new CompControllerImpl(compService, techService, locationService)
  lazy val authController = AuthController(authService)
  lazy val apiController: ApiController = new ApiControllerImpl(compService, techService, locationService)

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
