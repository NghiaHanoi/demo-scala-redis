package backend.dao

trait BaseDao[K, E]{
    def all():List[E]
    def create(c:E):K//newly K
    def remove(k:K):E//deleted E
    def get(key:K):E //got E
    def update(c:E):E//new E
    def search(e:E):List[E]//matching List[E]
}

