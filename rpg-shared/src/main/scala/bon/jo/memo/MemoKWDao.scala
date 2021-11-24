package bon.jo.memo

import bon.jo.dao.Dao

trait MemoKWDao extends Dao[Entities.MemoKeywords,Int]:
      def findByKeyWord(kws : String) : FL
