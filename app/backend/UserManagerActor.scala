package backend
import akka.actor.Props


/**
 * Not use at the moment
 * @author nghia
 *
 */
class UserManagerActor extends akka.actor.Actor {
  def receive = {
      case _ => //Do nothing
    }
}
object UserManagerActor{
  def props(): Props = Props(new UserManagerActor)
  
}