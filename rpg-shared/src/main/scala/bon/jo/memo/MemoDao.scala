package bon.jo.memo

import bon.jo.dao.Dao

trait MemoDao extends  Dao[Entities.Memo, Int]
