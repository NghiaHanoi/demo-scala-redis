package backend.dao
import scala.concurrent.Future
import akka.util.ByteString
trait BaseDao[K, E] {
  /* def all(userIdSeq : Seq[String]):List[Future[Option[String]]]*/
  def create(c: E): Future[Boolean]
  def remove(k: K): Future[Long]
  def get(key: K): Future[Option[String]]
  def update(c: E): Future[Boolean]
  /*def search(e:E):List[E]*/
}
