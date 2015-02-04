import backend.entity.UserEntity
import backend.dao.UserDao
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

class UpdatingUserFeatureSpec  extends BaseAcceptanceSpec {
  info("UserDao can update an user")
  info("So that user information can be changed")
    scenario("Request updating user"){
      Given("Creating a user and get user with that id")
      val udao = new UserDao()      
      When("Request new id from key user:id ")
      val newIdFuture:Future[Long]= udao.requestNewId()
      Then("got new id")      
      newIdFuture.onSuccess({
        case x if x >0 => {
          When("2.Succcess getting the new user id")
          Then("perform creating user with the new user id $s")
          val uEnt = UserEntity(x,"Nguyen Giao Bao",9)
          val userFuture:Future[Boolean] = udao.create(uEnt)
          userFuture.onSuccess({
            case u =>{
              When(s"Sucessfull creating user with id = $x")
              Then("update new name to 'Someone'")
              val newUser = UserEntity(x,"Someone", 9)
              Then("Do updating new information to database")
              val uFu:Future[Boolean] = udao.update(newUser)
              uFu.onSuccess({
                case s2 =>{
                  When ("Update to database success")
                  Then("Get back the updated entity from database")
                  val newUserFuture = udao.get(x)
                  newUserFuture.onSuccess({
                    case u2 =>{
                      When("Got new user from database")
                      Then ("convert data to JsonValue")
                      val newUserJson:String = u2.value
                      Then (s"Got new user json $newUserJson")
                      val newUserJsonVal:JsValue = Json.parse(newUserJson)
                      val newUserName = (newUserJsonVal \ "name" ).asOpt[String]
                      Then (s"Got new user name $newUserName")
                      assert(newUserName.value.equals("Someone")) 
                    }
                  })
                  newUserFuture.onFailure({
                    case t => t
                  })
                }
              })
              uFu.onFailure({
                case t=>t
              })                             
            }
          })          
          userFuture.onFailure({
            case t2=>{
              When("2. Failed getting lastest user")
              Then(t2.getMessage())
         }
      })
        }
      })      
      newIdFuture.onFailure({
        case t=>{
          When("2. Getting latest user id failed ")
          Then(t.getMessage)
         }
      })
    }
}