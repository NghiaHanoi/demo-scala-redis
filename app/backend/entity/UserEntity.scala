package backend.entity
import play.api.libs.json._
import play.api.libs.functional.syntax._

//a Companion Object
case class UserEntity(id:Long, name:String, age:Int)
object UserEntity{  
  val userReads: Reads[UserEntity] = (
  (__ \ "id").read[Long] and
  (__ \ "name").read[String] and
  (__ \ "age").read[Int]
)(UserEntity.apply _)

val userWrites: Writes[UserEntity] = (
  (__ \ "id").write[Long] and
  (__ \ "name").write[String] and
  (__ \ "age").write[Int]
)(unlift(UserEntity.unapply))

implicit val userEntityFormat: Format[UserEntity] =
  Format(userReads, userWrites)  
}
