package frontend
import play.api._
import play.api.libs.concurrent.Akka
import akka.cluster.Cluster
import java.net.URL
object ActorPlugin {

  private def actors(implicit app: Application) = app.plugin[ActorPlugin]
    .getOrElse(sys.error("Actors plugin not registered"))

  /**
   * Get the region manager client.
   */
  def userManagerClient(implicit app: Application) = actors.userManagerClient
}

/**
 * Manages the creation of actors in the web front end.
 *
 * This is discovered by Play in the `play.plugins` file.
 */
class ActorPlugin(app: Application) extends Plugin {

  private def system = Akka.system(app)

  override def onStart() = {
    if (Cluster(system).selfRoles.exists(r => r.startsWith("backend"))) {
      system.actorOf(backend.UserManagerActor.props(), "userManager")
    }
  }

  private lazy val userManagerClient = system.actorOf(UserManagerClientActor.props(), "userManagerClient")
}