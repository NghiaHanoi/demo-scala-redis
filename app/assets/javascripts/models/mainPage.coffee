define ["knockout"], (ko) ->

  class MainPageModel
    constructor: () ->
      # user id
      @id = ko.observable()
      @name = ko.observable()
      @age = ko.observable()
      @connect
      # connecting or reconnecting sessage
      @connecting = ko.observable()
      @disconnected = ko.observable(true)
      
      # close flag
      @closing = false

    # The user submit for create user
    createOrUpdate: ->      
      @connect()
      # TODO implement
    recvListUser:->
    recvUpdateUser:->
    recvDeleteUser:->
    recvSearchUser:->

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->      
      @email = "some"
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
	
	  # Handle the stream of feature updates
	  # Event including: update-user, delete-user, create-user, list-user, search-user
	  @ws.onmessage = (event) =>
	    json = JSON.parse(event.data)
	    if json.event == "user-create"	
	      recvCreateUser
		if json.event == "user-list" recvListUser
	    if json.event == "user-update" recvUpdateUser
	    if json.event == "user-delete" recvDeleteUser
	    if json.event == "user-search" recvSearchUser	            
	
	  # Disconnect the web socket
	  disconnect: ->
	    @closing = true
	    @ws.close()   

  return MainPageModel

