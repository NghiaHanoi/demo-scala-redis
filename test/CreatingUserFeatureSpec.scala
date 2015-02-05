import backend.dao.UserDao
import backend.entity.UserEntity
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString
import play.api.libs.json._

/**
 * User feature behavior check (Acceptance and Unit) based on FeatureSpec
 */
class CreatingUserFeatureSpec extends BaseAcceptanceSpec {
  info("As an User Dao")
  info("User Dao want to create an user")
  info("So user informtion can be store in DB for later usage")
  feature("User dao feature behavior") {
    scenario("Request creating user") {
      Given("A user dao instance")
      val udao = new UserDao()
      When("Peform request new user id ")
      val reqUId: Future[Long] = udao.requestNewId()
      Then("user id in future should not be null, or throw exception ")
      reqUId.onSuccess({
        case s => {
          println(s)
          assert(s > -1)
          When("on success of request user id, peform insert new user id ")
          val reqInsertId: Future[Long] = udao.insertNewId(s)
          Then("user id in future should not be null, or throw exception ")
          reqInsertId.onSuccess({
            case s2 =>
              println(s2)
              assert(s2 > -1)
              When("on success of insert user id, peform create new user")
              val user = new UserEntity(s, "Nguyen Van Nghia", 40)
              val reqCreateUser: Future[Boolean] = udao.create(user)
              Then("Create user shoud besucceed or else get exception")
              reqCreateUser.onSuccess({
                case s3 =>
                  Then("Suceessfull creating user")
              })
              reqCreateUser.onFailure({
                case t3 => {
                  Then(s"Failed creating user $t3.getMessage")
                }
              })
          })
          reqInsertId.onFailure({
            case t2 =>
              Then(s"Fail inserting new user id $t2.getMessage")
          })
        }
      })
      reqUId.onFailure({
        case t =>
          Then(s"Fail requesting new user id $t.getMessage")
      })
    }
  }
}