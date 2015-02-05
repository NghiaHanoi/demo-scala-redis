import backend.dao.UserDao
import backend.entity.UserEntity
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString
import play.api.libs.json._

/**
 * User feature behavior check (Acceptance and Unit) based on FeatureSpec
 */
class GettingUserFeatureSpec extends BaseAcceptanceSpec {
  info("As an User Dao")
  info("UserDao can get an user")
  info("So that user information can be viewed")

  feature("User dao feature behavior check") {
    scenario("2.Request getting an user") {
      Given("A user dao instance")
      val udao = new UserDao()
      //getting new Id
      When("Get the last id from key user:id ")
      val latestIdFuture: Future[Option[String]] = udao.getLastestId()
      Then("The last new user id should be available or throw exception")
      latestIdFuture.onSuccess({
        case s => {
          When("2.Succcess getting the latest new user id")
          Then("2.The lastest user id should greater than 0")
          assert(s.value.toInt > 0)
          When(s"Get latest id success")
          Then("perform getting user with the latest user id $s.value")
          val uId: Long = s.value.toLong
          val userFuture: Future[Option[String]] = udao.get(uId)
          userFuture.onSuccess({
            case u => {
              When("2.Sucessfull getting user")
              Then("can convert byte string (Json) to UserEntity")
              Then("instance, name of user should be 'Nguyen Van Nghia'")
              val userJson: String = u.value
              Then(s"Got original user json $userJson")
              val jsonVal: JsValue = Json.parse(userJson)
              val maybeName = (jsonVal \ "name").asOpt[String]
              Then(s"Got user name $maybeName")
              assert(maybeName.value.equals("Nguyen Van Nghia"))
              val jsonString: String = Json.stringify(jsonVal)
              Then(s"Got Json String back $jsonString")
              assert(jsonString === userJson)

              val jsRet: JsResult[UserEntity] =
                Json.fromJson[UserEntity](jsonVal)
              jsRet.foreach { x =>
                {
                  When("Got the entity result ")
                  Then(s"Entity name should be 'Nguyen Van Nghia': $x")
                  assert(x.name.equals("Nguyen Van Nghia"))
                }
              }
            }
          })
          userFuture.onFailure({
            case t2 => {
              When("2. Failed getting lastest user")
              Then(t2.getMessage())
            }
          })
        }
      })
      latestIdFuture.onFailure({
        case t => {
          When("2. Getting latest user id failed ")
          Then(t.getMessage)
        }
      })
    }
  }
}