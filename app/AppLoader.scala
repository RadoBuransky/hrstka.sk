import controllers.{AppController, TechController}
import repositories.mongoDb.{MongoTechRepository, MongoTechVoteLogRepository}
import repositories.{TechRepository, TechVoteLogRepository}
import services.TechService
import services.impl.TechServiceImpl

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteLogRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteLogRepository: TechVoteLogRepository = new MongoTechVoteLogRepository
}
