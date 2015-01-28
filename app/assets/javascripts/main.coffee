#
# The main entry point into the client side. Creates a new main page model and binds it to the page.
#
require.config {
  paths: {
    mainPage: "./models/mainPage"
    bootstrap: "../lib/bootstrap/js/bootstrap"
    jquery: "../lib/jquery/jquery"
    knockout: "../lib/knockout/knockout"
    leaflet: "../lib/leaflet/leaflet"
  }
  shim: {
    bootstrap: {
      deps: ["jquery"],
      exports: "$"
    }
    jquery: {
      exports: "$"
    }
    knockout: {
      exports: "ko"
    }
  }
}

require ["knockout", "mainPage", "bootstrap"], (ko, MainPageModel) ->

  model = new MainPageModel
  model.userJson = ko.dependentObservable(() => JSON.parse "[{\"id\":\"" +model.userid() + "\", \"name\":\"" + model.username()+"\", \"age\":\""+model.age()+"\"}]")                                                
  ko.applyBindings(model)

