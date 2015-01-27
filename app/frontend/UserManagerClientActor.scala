package frontend

import akka.actor.Actor
import akka.actor.Props

class UserManagerClientActor extends Actor {

}
object UserManagerClientActor{
  def props(): Props = Props(new UserManagerClientActor)
}