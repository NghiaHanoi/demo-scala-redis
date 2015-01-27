package frontend
import play.api._
import play.api.libs.concurrent.Akka
import java.net.URL

/**
 * Use this class in cluster environment
 * If we do not use actor cluster we just associate UserManagerActor to 
 * a local val, no need to start this actor when system starting up. 
 * @author nghia
 *
 */
object ActorPlugin {

  private def actorPlugin(implicit app: Application) = app.plugin[ActorPlugin]
    .getOrElse(sys.error("Actors plugin not registered"))

  /**
   * Get the region manager client.
   */
  def userManagerClient(implicit app: Application) = actorPlugin.userManagerClient
}

/**
 * Manages the creation of actors in the web front end.
 *
 * This is discovered by Play in the `play.plugins` file.
 */
class ActorPlugin(app: Application) extends Plugin {

  private def system = Akka.system(app)

  override def onStart() = {    
      //comment out, use in actor cluster only  
      //system.actorOf(backend.UserManagerActor.props(), "userManager")    
  }
  
  private lazy val userManagerClient = system.actorOf(UserManagerClientActor.props(), "userManagerClient")
}