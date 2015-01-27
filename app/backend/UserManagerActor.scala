package backend
import akka.actor.Props
class UserManagerActor extends akka.actor.Actor {

}
object UserManagerActor{
  def props(): Props = Props(new UserManagerActor)
}