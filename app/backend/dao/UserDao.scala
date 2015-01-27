package backend.dao
import backend.entity.UserEntity

class UserDao(id:Long, user:UserEntity ) extends BaseDao[Long, UserEntity]{
  override def all():List[UserEntity] = {
    //redis for getting data from database 0 go here
    List(new UserEntity("0".asInstanceOf, "tets_name", 25))//Test
  }
  override def create(e:UserEntity =>{
    //TODO
  })
  override def remove(key:Long =>{
    //TODO
  })
  override def get(key:Long => {
    //TODO
  })
  override def update(c:UserEntity => {
    //TODO
  })
  override def search:List[UserEntity] = {
    List(new UserEntity("0".asInstanceOf, "tets_name", 25))//Test
  }
}