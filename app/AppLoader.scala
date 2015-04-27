import controllers.{AuthController, AppController, CompController, TechController}
import repositories.mongoDb._
import repositories._
import services.impl.{AuthServiceImpl, CompServiceImpl, TechServiceImpl}
import services.{AuthService, CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)
  lazy val compController = CompController(compService, techService)
  lazy val authController = AuthController(authService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteRepository, techVoteLogRepository)
  lazy val compService: CompService = new CompServiceImpl(compRepository, techService)
  lazy val authService: AuthService = new AuthServiceImpl(userRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteRepository: VoteRepository = MongoTechVoteRepository
  lazy val techVoteLogRepository: VoteLogRepository = MongoTechVoteLogRepository
  lazy val compRepository: CompRepository = new MongoCompRepository
  lazy val userRepository: UserRepository = new MongoUserRepository

}
