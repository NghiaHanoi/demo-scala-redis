import backend.dao.UserDao
import scala.concurrent.ExecutionContext.Implicits.global
import backend.entity.UserEntity
import play.api.libs.json._
import scala.collection.mutable.MutableList
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent._
import scala.util._

class SearchingUserFeatureSpec extends BaseAcceptanceSpec {
  info("UserDao can search users with some user figure such as name or age")
  info("So that user information can be easy to find")
  scenario("Request searching by user id") {
    Given("A user dao and search map")
    val udao = new UserDao()
    //TODO change user id to match your database
    val u: UserEntity = UserEntity(28, "-1", -1)

    When("Peform reading all user id user ")
    var listUsers: MutableList[UserEntity] = MutableList[UserEntity]()

    var futures: MutableList[Future[Option[String]]] =
      MutableList[Future[Option[String]]]()
    val uIdListFu = udao.allUserId()
    uIdListFu.onSuccess(
      {
        case x => {
          When("get list user id success")
          Then(s"get list future of user, $x")
          var remaining = new AtomicInteger(x.size)
          val p = Promise[Option[String]]()
          for {
            id <- x
          } yield {
            if (id.toLong == u.id) {
              futures += udao.get(id.toLong)
              val sz = futures.size
              Then(s" got $sz future in future list")
            }
          }
          futures.foreach {
            _ onComplete {
              case s @ Success(_) => {
                When(s"Future $remaining success complete")
                if (remaining.decrementAndGet() > 0) {
                  val jsonVal = s.get.value
                  Then("convert Json string to entity")
                  listUsers += Json.fromJson[UserEntity](
                    Json.parse(jsonVal)).get
                  When(s"Got the entities $listUsers ")
                  Then(s"Entity name should be the same as condition")
                  assert(listUsers.last.id == u.id)
                } else {
                  // Arbitrarily return the final success
                  p tryComplete s
                }
              }
              case f @ Failure(_) => {
                p tryComplete f
              }
            }
          }
          p.future.onComplete {
            case x @ Success(_) => {
              When("complete all future success")
              Then(s"list of user must be greater than zero $listUsers")
              //cannot test in the non-blocking-way
              //assert(listUserJsons.size>0)
            }
            case x @ Failure(_) => {
              When("complete all future with some or all failure")
              x
            }
          }
        }
      })
    uIdListFu.onFailure(
      {
        case t =>
          When("get list user id error")
          Then(s"Error details: $t")
      })
  }

}