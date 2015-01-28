define ["knockout"], (ko) ->

  class MainPageModel
    constructor: () ->
      # user id
      @id = ko.observable()
      @name = ko.observable()
      @age = ko.observable()
      
      # connecting or reconnecting sessage
      @connecting = ko.observable()
      @disconnected = ko.observable(true)
      
      # close flag
      @closing = false
    
    # The user submit for create user
    createOrUpdate: (user) ->      
      if(self.disconnected)
        @connect()
      user = JSON.parse "[]"
      # Send the user to server for update as non-blocking way
      @ws.send(JSON.stringify
        event: "user-create"
        user:
          type: "User"
         coodinates: [position.coords.longitude, position.coords.latitude]
      )
              
    # TODO implement   
    recvAllUser: ->
      @connect()
    
    recvUpdateUser: ->
      @connect()
    
    recvDeleteUser: ->
      @connect()
    
    recvSearchUser: ->
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->      
      @connecting("Connecting...")
      @disconnected(null)      
      @ws = new WebSocket($("meta[name='websocketurl']").attr("content"))      
      # Websocket open handler
      @ws.onopen = (event) =>
      @connecting(null)      
      @ws.onclose = (event) =>
      # Need to handle reconnects in case of errors
        if (!event.wasClean && !self.closing)
          @connect()
          @connecting("Reconnecting...")
        else
          @disconnected(true)
          @closing = false  
        # Event including: update-user, delete-user, create-user, list-user, search-user
      @ws.onmessage = (event) => 
        json = JSON.parse(event.data)
        if json.event == "user-create" 
          @recvCreateUser()
        if json.event == "user-list"
          @recvAllUser()
        if json.event == "user-update" 
          @recvUpdateUser()
        if json.event == "user-delete"
          @recvDeleteUser()
        if json.event == "user-search" 
          @recvSearchUser() 
          
    # Disconnect the web socket
    disconnect: ->
      @closing = true
      @ws.close()   

  return MainPageModel

