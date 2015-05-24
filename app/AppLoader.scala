import controllers._
import controllers.auth.{AuthTechController, AuthTechControllerImpl, AuthCompControllerImpl, AuthCompController}
import repositories.mongoDb._
import repositories._
import services.impl.{LocationServiceImpl, AuthServiceImpl, CompServiceImpl, TechServiceImpl}
import services.{LocationService, AuthService, CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = new AppControllerImpl(locationService, techService)
  lazy val compController: CompController = new CompControllerImpl(compService, authService, techService, locationService)
  lazy val authController = new AuthControllerImpl(authService, locationService, techService)
  lazy val apiController: ApiController = new ApiControllerImpl(compService, techService, locationService)

  // Controllers that require authorization
  lazy val authCompController: AuthCompController = new AuthCompControllerImpl(compService, authService, techService, locationService)
  lazy val authTechController: AuthTechController = new AuthTechControllerImpl(authService, locationService, techService)

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
