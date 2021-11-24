package bon.jo.memo

trait Memo[A,K]:
    val content : A
    val keysWords : Set[K]

object Memo:
  case class MemoImpl[A,K]( content: A , keysWords : Set[K]) extends Memo[A,K]

  def apply[A,K]( content: A , keysWords : Set[K]): Memo[A,K] = MemoImpl(content,keysWords)
  trait KeyMatch[K]:
    def apply(ref : K)(k : K) : Boolean
  def find[K](k : K,iterable: Iterable[Memo[_,K]])(keyMatch : KeyMatch[K]): List[Memo[_,K]]=
      iterable.filter(m=>m.keysWords.exists(keyMatch(k))).toList
