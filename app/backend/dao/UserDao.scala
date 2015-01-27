package backend.dao
import backend.entity.UserEntity
import redis.RedisClient
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import redis.RedisClient
import akka.util.ByteString
import play.api.libs.json._
import scala.collection.immutable.Map
import scala.collection.immutable.List
import redis.api.strings.Incr
import redis.api.strings.Incr

/**
 * Hard code for testing purpose
 * @author nghia
 *
 */
class UserDao extends BaseDao[Long, UserEntity]{
  import UserDao._
  /************************************************************************************************
   * Redis DB Design Rule: following K-V pattern will be applied:
   * - Use key "user:id" to store current user id in to list, new user id will be increased using INCR command
   * - Use key "user:all" to store all user id in a List, which have been increased (created using LPUSH, LGET)
   * - Use key "user:<id>:data" -> list to store data for each user(using HMGET, HMSET).
   ************************************************************************************************/
  val userIdKey:String = "user:id"
  val userIdAllKey = "user:all"
  def userDataKey(id:Long):String = {
      s"user:$id:data"  
  }
  
  implicit val akkaSystem = akka.actor.ActorSystem()
  def all():List[UserEntity] = {
    //Step 1: get all id from user:all: lrange user:id 0 -1
    //Step 2: go through all key and get user data
    //Step 3: build the user list   
  val redis = new RedisClient()
  val allUserId = redis.lrange(userIdAllKey, 0, -1)
  val totalRet:Map[String,String] = Map[String, String]()
  val retListUser:List[UserEntity] = List[UserEntity]() 
  
  allUserId.onSuccess({
    case x => {
      val r = for{
        x1 <- x        
      } yield {
       val userData = redis.hgetall(userDataKey(x1.toString().toLong))
       val newMap:Map[String, String] = Map[String, String]()
       userData.onSuccess({
         case ur => { 
            for{u<-ur}yield{
              newMap ++ Map(u._1-> u._2.toString())
             }
           totalRet ++ newMap
         }
         //Convert to List[UserEntity], expected Json result(Map[String, String]) should be
         // Map("SomeId", "{"id":"123456",name:"Some name", age:34}" } this help us to convert 
         // from this map values to Json object
         val listUsers:List[UserEntity] = List[UserEntity]()
         for{
           u<-totalRet
         }yield{
           val userEnt:UserEntity = Json.fromJson[UserEntity](Json.parse(u._2)).get
           retListUser ++ List(userEnt)
         }
       })
       userData.onFailure({
         case t => println(t)
       })
      }
    }
  })
  allUserId.onFailure({
     case t => println(t)
   }) 
  retListUser
  }
  def create(e:UserEntity):Long ={
    /*
     * Step to process
     * 1. Increase value in user:id (INCR)
     * 2. Push new id to user:all key (LPUSH)
     * 3. Set user detail to "user:<id>:data" key (HMSET)
     */
    val redis = new RedisClient()
    val newId = redis.incr(userIdKey)
    var newIdLong:Long = -1
    newId.onSuccess({
      case s =>
      newIdLong = s
      val future2 = redis.lpush[String](userIdAllKey, s.toString())
      future2.onComplete { 
        x => {
          val userDatKey:String = userDataKey(newIdLong)
          val userDetails:Map[String,String] = Map[String,String]()
          userDetails ++ Map(newId.toString() -> s"{'id':'$e.id','name':'$e.name','age':'$e.age'}" )             
          val future2 = redis.hmset[String](userDatKey, userDetails)
        } 
       }
      future2.onFailure({
        case t =>
        println(s"Error adding user to database $t")
      })
    })   
    newId.onFailure({
        case t =>
        println(s"Error adding user to database $t")
    })
    newIdLong    
  }
  def remove(key:Long):UserEntity ={
    val redis = new RedisClient()
    val delFuture = redis.del(key.toString())
    var retUser:UserEntity = null
    delFuture.onSuccess({
      case s =>
      println(s"Delete user $key successfully")
      retUser = get(key)      
    })
    delFuture.onFailure({
      case t =>
      println(s"Delete user $key failed, cause by $t")
      
    })    
    retUser
  }
  def get(key:Long):UserEntity = {
    val redis = new RedisClient()
    val getFu = redis.get(key.toString())
    getFu.onSuccess({
      case s =>
      //TODO to be continue
    })
    getFu.onFailure({
      case t =>
      println(s"Get user $key failed, cause by $t")      
    })
    new UserEntity("0".asInstanceOf, "tets_name", 25)
  }
  def update(c:UserEntity):UserEntity = {
    //TODO Implement to override the hard code 
    new UserEntity("0".asInstanceOf, "tets_name", 25)
  }
  def search(user:UserEntity):List[UserEntity] = {
    //TODO Implement to override the hard code
    List(new UserEntity("0".asInstanceOf, "tets_name", 25))
  }
}
object UserDao{  
  
}