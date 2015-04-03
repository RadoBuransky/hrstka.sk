import controllers.{AppController, CompController, TechController}
import repositories.mongoDb._
import repositories._
import services.impl.{CompServiceImpl, TechServiceImpl}
import services.{CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)
  lazy val compController = CompController(compService, techService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteRepository, techVoteLogRepository)
  lazy val compService: CompService = new CompServiceImpl(compRepository, compTechRepository, techService)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteRepository: VoteRepository = MongoTechVoteRepository
  lazy val techVoteLogRepository: VoteLogRepository = MongoTechVoteLogRepository
  lazy val compRepository: CompRepository = new MongoCompRepository
  lazy val compTechRepository: CompTechRepository = new MongoCompTechRepository
}
