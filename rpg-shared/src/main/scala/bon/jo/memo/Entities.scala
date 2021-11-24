package bon.jo.memo

object Entities:

  case class Memo(id: Option[Int], title: String, content: String, memoType: MemoType):
    def this(title: String, content: String) = this(None, title, content, MemoType.Text)
    def this(title: String, content: String, memoType: MemoType) = this(None, title, content, memoType)

//  object MemoS {
//    def tupled_(memo: Memo): Option[(Option[Int], String, String, String)] = {
//      Memo.unapply(memo)
//    }
//    def untupled_(z: (Option[Int], String, String, String)): Memo = Memo.tupled(z._1,z._2,z._3,MemoType(z._4))
//  }
// Option(memo.id,memo.title,memo.content,memo.memoType.name )
  case class KeyWord(id: Option[Int], value: String)

  case class MemoKeywordRel(memo: Int, keyWord: Int)

  case class MemoKeywords(memo: Memo, keyWords: Set[KeyWord])



  enum MemoType:
    val name: String = toString
    case  Text 

    case  Json

  
