import backend.entity.UserEntity
import backend.dao.UserDao
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author nghia
 * Check delete use behavior of user dao
 *
 */
class DeletingUserFeatureSpec extends BaseAcceptanceSpec {
  info("UserDao can delete an user")
  info("So that user information can be free from storage")
  scenario("Request deleting user") {
    Given("An instance of user dao")
    val udao = new UserDao()
    When("Getting latest user id")
    val lastUIdFuture = udao.getLastestId()
    lastUIdFuture.onSuccess({
      case s => {
        Then("On success getting last user id, delete the last user from Redis")
        val delUserFuture = udao.remove(s.value.toLong)
        delUserFuture.onSuccess({
          case s2 => {
            When("Delete user success, get the user from DB with the user id, to make sure user does not exist")
            val getUserFuture = udao.get(s.value.toLong)
            getUserFuture.onSuccess({
              case s3 =>
                When("Get user success")
                Then(s"User json string should be None")
                assert(s3 == None)

            })
            getUserFuture.onFailure({
              case t3 =>
                When("Get user which has been deleted failed")
                Then(s" Error details: $t3.getMessage")
            })
          }
        })
        delUserFuture.onFailure({
          case t2 =>
            When("Delete uder failed")
            Then(s" Error details: $t2.getMessage")
        })
      }
    })
    lastUIdFuture.onFailure({
      case t =>
        When("Get last user id failed")
        Then(s" Error details: $t.getMessage")
    })
  }
}