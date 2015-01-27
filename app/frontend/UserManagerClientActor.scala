package frontend

import akka.actor.Actor
import akka.actor.Props
import backend.dao.UserDao
import frontend.ClientConnectionActor.{ClientEvent,UpdateUserEvent,CreateUserEvent, DeleteUserEvent, ListUserEvent, SearchUserEvent }

class UserManagerClientActor extends Actor {
  import UserManagerClientActor._
  def receive = {
    case UpdateUserEvent(user)=>{
        val retUser = userDao.update(user)
        sender ! retUser //send updated user back to client
    }
    case CreateUserEvent(user)=>{
      val retUser = userDao.create(user)
      sender ! retUser //send updated user back to client
    }
    case DeleteUserEvent(id)=>{
      val ret = userDao.remove(id)
      sender ! ret //send updated user back to client
    }
    case ListUserEvent =>{
      val ret = userDao.all()
      sender ! ret //send updated user back to client
    }
    case SearchUserEvent(user)=>{
      val ret = userDao.search(user)
      sender ! ret //send updated user back to client
    }
    case _ => {//Invalid message
      println("Invalid message sent from client Websocket!")
    }
  }
}
object UserManagerClientActor{
  implicit def userDao:UserDao = new UserDao
  def props(): Props = Props(new UserManagerClientActor)
  
}