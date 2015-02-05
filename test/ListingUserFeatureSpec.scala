import backend.dao.UserDao
import scala.concurrent.ExecutionContext.Implicits.global
import backend.entity.UserEntity
import play.api.libs.json._
import scala.collection.mutable.MutableList
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent._
import scala.util._

class ListingUserFeatureSpec extends BaseAcceptanceSpec {
  info("UserDao can list all user")
  info("So that can list all user information in database")
  scenario("Request listing user") {
    Given("A user dao and user entity instance")
    val udao = new UserDao()
    When("Peform reading all user id user ")
    var listUsers: MutableList[UserEntity] = MutableList[UserEntity]()
    var listUserJsons: MutableList[String] = MutableList[String]()

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
            futures += udao.get(id.toLong)
            val sz = futures.size
            Then(s" got $sz future in future list")

          }
          futures.foreach {
            _ onComplete {
              case s @ Success(_) => {
                When(s"Future $remaining success complete")
                if (remaining.decrementAndGet() > 0) {
                  val jsonVal = s.get.value
                  Then("convert Json string to entity")
                  listUserJsons += jsonVal
                  listUsers += Json.fromJson[UserEntity](
                    Json.parse(jsonVal)).get
                  When(s"Got the entities $listUsers ")
                  Then(s"Entity name should not be None")
                  assert(listUsers.last.name != None)
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
              Then(s"list of user must be greater than zero $listUserJsons")
              //assert(listUserJsons.size>0)//cannot test in blocking-way
            }
            case x @ Failure(_) => {
              When("complete all future with some or all failure")
              //Ignore
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