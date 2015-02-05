package backend.entity
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UserEntity(id: Long, name: String, age: Int)

/**
 * a user Companion Object of class UserEntity
 * @author nghia
 *
 */
object UserEntity {
  def userReads: Reads[UserEntity] = (
    (__ \ "id").read[Long] and
    (__ \ "name").read[String] and
    (__ \ "age").read[Int])(UserEntity.apply _)

  def userWrites: Writes[UserEntity] = (
    (__ \ "id").write[Long] and
    (__ \ "name").write[String] and
    (__ \ "age").write[Int])(unlift(UserEntity.unapply))

  implicit val userEntityFormat: Format[UserEntity] =
    Format(userReads, userWrites)
}
