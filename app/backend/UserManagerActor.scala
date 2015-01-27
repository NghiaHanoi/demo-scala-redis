package backend
import akka.actor.Props


/**
 * Not use at the moment
 * @author nghia
 *
 */
class UserManagerActor extends akka.actor.Actor {

}
object UserManagerActor{
  def props(): Props = Props(new UserManagerActor)
}