package frontend
import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter
import play.api.Application
import backend.entity.UserEntity

object ClientConnectionActor {
  def props(upstream: ActorRef, userManagerClient: ActorRef): Props = {
    Props(new ClientConnectionActor(upstream, userManagerClient))
  }
  sealed trait ClientEvent
  case class UpdateUserEvent(user:UserEntity) extends ClientEvent
  case class CreateUserEvent(user:UserEntity) extends ClientEvent
  case class DeleteUserEvent(id:Long) extends ClientEvent
  case class ListUserEvent() extends ClientEvent
  //Search base on value set to UserEntity, property value = -1 mean not search on that properties
  case class SearchUserEvent(user:UserEntity) extends ClientEvent
  
  object ClientEvent{
    //Implement JSON Serialize / Deserialize (required), need to implement 
    //Read or Format implicit method
    implicit def clientEventFormat: Format[ClientEvent] = Format(
        //Read function, read from Json and parse event object
      (__ \ "event").read[String].flatMap {
        case "update-user" =>{
          val format:Format[UpdateUserEvent] = UpdateUserEvent.updateUserFormat
          format.map(identity)        
        } 
        case "create-user" => {
         val format:Format[CreateUserEvent] = CreateUserEvent.createUserFormat
         format.map(identity) 
        }
        case "delete-user" => {
         val format:Format[DeleteUserEvent] = DeleteUserEvent.deleteUserFormat
         format.map(identity) 
        }
        case "list-user" =>{
         val format:Format[ListUserEvent] = ListUserEvent.listUserFormat
         format.map(identity) 
        } 
        case "search-user" => SearchUserEvent.searchUserFormat.map(identity)
        case other => Reads(_ => JsError("Unknown client event: " + other))
      },
      //Write Function
      Writes {
        case u: UpdateUserEvent => UpdateUserEvent.updateUserFormat.writes(u)
        case c: CreateUserEvent => CreateUserEvent.createUserFormat.writes(c)
        case d: DeleteUserEvent => DeleteUserEvent.deleteUserFormat.writes(d)
        case l: ListUserEvent   => ListUserEvent.listUserFormat.writes(l)
      }
    )
    //Implicit method required by Application Controller to format WebsocketFrame 
    implicit def clientEventFrameFormatter: FrameFormatter[ClientEvent] = FrameFormatter.jsonFrame.transform(
      clientEvent => Json.toJson(clientEvent),
      json => Json.fromJson[ClientEvent](json).fold(
        invalid => throw new RuntimeException("Bad client event on WebSocket: " + invalid),
        valid => valid
      )
    )
  }
  object UpdateUserEvent {
    
  implicit def updateUserFormat: Format[UpdateUserEvent] = (
      (__ \ "event").format[String] ~
        (__ \ "user").format[UserEntity]
      ).apply(
          {
            case ("update-user", user) => UpdateUserEvent(user)
          }, userUpdateEvent => ("update-user", userUpdateEvent.user)
    )
  }
  object CreateUserEvent {
    implicit def createUserFormat: Format[CreateUserEvent] = (
      (__ \ "event").format[String] ~
        (__ \ "user").format[UserEntity]
      ).apply(
          {
            case ("create-user", user) => CreateUserEvent(user)
          }, createUserEvent => ("create-user", createUserEvent.user)
    )
  }
  object DeleteUserEvent {
    implicit def deleteUserFormat: Format[DeleteUserEvent] = (
      (__ \ "event").format[String] ~
        (__ \ "user").format[Long]
      ).apply(
          {
            case ("update-user", userId) => DeleteUserEvent(userId)
          }, deleteUserEvent => ("update-user", deleteUserEvent.id)
    )
  }
  object ListUserEvent {
    implicit def listUserFormat: Format[ListUserEvent] =(
      (__ \ "event").format[String] ~
        (__ \ "user").format[Long]//should be zero
      ).apply(
          {
            case ("update-user", 0) => ListUserEvent()
          }, listuserEvent => ("list-user", 0)
    )
  }
  object SearchUserEvent {
    implicit def searchUserFormat: Format[SearchUserEvent] = (
      (__ \ "event").format[String] ~
        (__ \ "user").format[UserEntity]
      ).apply(
          {  case ("search-user", user) => SearchUserEvent(user)
          }, searchEvent => ("user-positions", searchEvent.user)
    )
  }
}

class ClientConnectionActor(upstream: ActorRef, userManagerClient: ActorRef) extends Actor{
  import ClientConnectionActor._
  def receive = {
    case updateEvent @ UpdateUserEvent(user)=>{
      userManagerClient.forward(updateEvent)      
    }
    case createEvent @ CreateUserEvent(user)=>{
      userManagerClient.forward(createEvent)
    }
    case deleteEvent @ DeleteUserEvent(id)=>{
      userManagerClient.forward(deleteEvent)
    }
    case listEvent @ ListUserEvent =>{
      userManagerClient.forward(listEvent)
    }
    case searchEvent @ SearchUserEvent(user)=>{
      userManagerClient.forward(searchEvent)
    }
    case _ => {//Invalid message
      println("Invalid message sent from client Websocket!")
    }
  }
}