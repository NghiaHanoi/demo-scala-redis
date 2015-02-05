package backend.dao
import backend.entity.UserEntity
import scala.concurrent.ExecutionContext.Implicits.global
import redis.RedisClient
import play.api.libs.json._
import redis.api.strings.Incr
import scala.concurrent._
import scala.util.Success
import scala.util.Failure
import backend.entity.UserEntity
/**
 * Data Access Object for user application
 * @author nghia
 *
 */
class UserDao extends BaseDao[Long, UserEntity] {
  import UserDao._

  /**
   * **************************************************************************
   * Redis DB Design Rule: following K-V pattern will be applied:
   * - Use key "user:id" to store current user id in to list, new user id will
   *   be increased using INCR command.
   * - Use key "user:all" to store all user id in a List,
   *   which have been increased (created using LPUSH, LGET)
   * - Use key "user:<id>:data" -> list to store data for each user(using HMGET,
   *   HMSET).
   * **************************************************************************
   */
  val userIdKey: String = "user:id"
  val userFieldName: String = "users"
  val userIdAllKey = "user:all"
  def userDataKey(id: Long): String = {
    s"user:$id:data"
  }

  implicit val akkaSystem = akka.actor.ActorSystem()
  implicit lazy val redis = new RedisClient()

  def allUserId(): Future[Seq[String]] = {
    val allUserId = redis.lrange[String](userIdAllKey, 0, -1)
    allUserId
  }

  /**
   * Cannot use as stand alone function
   * @param userIdSeq
   * @return
   */
  def all(userIdSeq: Seq[String]): List[Future[Option[String]]] = {
    val userDatFutureList: List[Future[Option[String]]] =
      List[Future[Option[String]]]()
    val r = for {
      x1 <- userIdSeq
    } yield {
      val uDatKey = userDataKey(x1.toLong)
      userDatFutureList ++ List(redis.hget[String](uDatKey, userFieldName))
    }
    userDatFutureList
  }

  def getLastestId(): Future[Option[String]] = {
    val lastestId = redis.get[String](userIdKey)
    lastestId
  }
  def requestNewId(): Future[Long] = {
    val newId = redis.incr(userIdKey)
    newId
  }
  def insertNewId(id: Long): Future[Long] = {
    val future: Future[Long] = redis.lpush[Long](userIdAllKey, id)
    future
  }
  def create(e: UserEntity): Future[Boolean] = {
    val userDatKey: String = userDataKey(e.id)
    val userDataString: String = "{\"id\":" + e.id + ",\"name\":\"" + e.name +
      "\",\"age\":" + e.age + "}"
    val fret = redis.hset[String](userDatKey, userFieldName, userDataString)
    fret
  }
  def remove(key: Long): Future[Long] = {
    val userDatKey: String = userDataKey(key)
    val tx = redis.transaction()
    tx.watch(userDatKey)
    val delFuture = tx.del(userDatKey)
    delFuture.onSuccess({
      case s =>
        println(s"Delete userDatKey $userDatKey successfully")
        val delUserId = tx.del(key.toString())
        delUserId.onSuccess({
          case s =>
            val decFu = tx.decr(userIdKey)
            decFu.onSuccess({
              case s =>
            })
        })
        delUserId.onFailure({
          case t =>
            println(s"Error remove user id from usr:all $t")
        })

    })
    delFuture.onFailure({
      case t =>
        println(s"Delete user $key failed, cause by $t")
    })
    tx.exec()
    delFuture
  }
  def get(key: Long): Future[Option[String]] = {
    val uDatKey = userDataKey(key)
    val getFu = redis.hget[String](uDatKey, userFieldName)
    getFu
  }
  def update(u: UserEntity): Future[Boolean] = {
    val tx = redis.transaction()
    val uDatKey = userDataKey(u.id)
    val userDataString = "{\"id\":" + u.id + ",\"name\":\"" + u.name +
      "\",\"age\":" + u.age + "}"
    tx.watch(uDatKey)
    val fuUpdate = tx.hset[String](uDatKey, userFieldName, userDataString)
    tx.exec()
    fuUpdate
  }
  /**
   * Cannot use as single function here
   * @param u
   * @return
   */
  def search(u: UserEntity): List[UserEntity] = {
    var retList: List[UserEntity] = List[UserEntity]()
    //    val allEntity = all()
    //    val t = for {
    //      e <- allEntity
    //      e2 <- allEntity if (  (u.id > 0 && u.id == e.id)
    //          || (!u.name.equals("-1") && e.name.equals(u.name))
    //          || (u.age > 0 && e.age == u.age)
    //          )
    //    } yield {retList ++ List(e2)}
    retList
  }
}
object UserDao {

}