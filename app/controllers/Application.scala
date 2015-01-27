package controllers

import play.api._
import play.api.mvc._
import frontend.ClientConnectionActor
import frontend.ClientConnectionActor.ClientEvent
import play.api.Play.current
import akka.actor.{Props, ActorRef, Actor}
import frontend.ActorPlugin

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }
  
  //upstream is WebsocketHandler Props instance
  def ws = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>
    ClientConnectionActor.props(upstream, ActorPlugin.userManagerClient)
  }
}