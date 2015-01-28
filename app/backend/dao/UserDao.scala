package backend.dao
import backend.entity.UserEntity
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import redis.RedisClient
import akka.util.ByteString
import play.api.libs.json._
import redis.api.strings.Incr

/**
 * Data Access Object for user application
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
    //1. Remove key data from "use:<id>:data"
    //2. Remove key in "user:all"
    //3. Decrease id in "usr:id"
    val userDatKey:String = userDataKey(key)
    val redis = new RedisClient()
    val tx = redis.transaction()
    tx.watch(key.toString())
    //step-1
    val delFuture = tx.del(userDatKey)
    var retUser:UserEntity = null
    delFuture.onSuccess({
      case s =>
      println(s"Delete userDatKey $userDatKey successfully")
      //step-2 
      val delUserId = tx.del(key.toString())
      delUserId.onSuccess({
        case s =>
          val decFu = tx.decr(userIdKey)
         decFu.onSuccess({
           case s =>
             //do nothing
         })
          retUser = get(key)         
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
    retUser
  }
  def get(key:Long):UserEntity = {
    //Step-1: build up user data key and get value from "user:<id>:data"
    //Step-2: Parse json value to UserEntity
    val redis = new RedisClient()    
    val uDatKey = userDataKey(key)
    val getFu = redis.get(uDatKey)  
    var userEnt:UserEntity = null
    getFu.onSuccess({
      case s =>
      //Step-2 : parse Json to entity
      val userJson:String = s.toString()
      userEnt = Json.fromJson[UserEntity](Json.parse(userJson)).get
    })
    getFu.onFailure({
      case t =>
      println(s"Get user $key failed, cause by $t")
      return null
    })
    userEnt
  }
  def update(u:UserEntity):UserEntity = {
    //Step-1 build key then build Json String value for key "user:<id>:data" from input parameter
    //Step-2 use HMSET to update the key
    val redis = new RedisClient()
    val tx = redis.transaction()
    val uDatKey = userDataKey(u.id)
    val userDetailValue = s"{'id':'$u.id','name':'$u.name','age':'$u.age'}"
    var ret:UserEntity = null
    tx.watch(uDatKey)
    val fuUpdate = tx.hmset(uDatKey, Map("id" ->u.id.toString(),"name"->u.name, "age" -> u.age.toString()))
    fuUpdate.onComplete { x => 
      if(x.get == true) ret = u        
     }
    tx.exec()
    ret
  }
  def search(u:UserEntity):List[UserEntity] = {
    var retList:List[UserEntity] = List[UserEntity]()
    val allEntity = all()
    val t = for {
      e <- allEntity
      e2 <- allEntity if (  (u.id > 0 && u.id == e.id)
          || (!u.name.equals("-1") && e.name.equals(u.name))
          || (u.age > 0 && e.age == u.age)
          )
    } yield {retList ++ List(e2)}
    retList
  }
}
object UserDao{  
  
}