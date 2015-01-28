package controllers

import play.api.mvc._
import frontend.{ClientConnectionActor, ActorPlugin}
import play.api.Play.current
import frontend.ClientConnectionActor.ClientEvent

object Application extends Controller {

  def index = Action { implicit req =>
    Ok(views.html.index())
  }
  
  //upstream is WebsocketHandler Props instance
  def userws() = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>{
    ClientConnectionActor.props(upstream, ActorPlugin.userManagerClient)
    } 
  }
}