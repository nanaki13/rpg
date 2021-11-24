package bon.jo.memo.ui

import bon.jo.app.User
import bon.jo.game.html.Template
import bon.jo.game.html.Template.XmlTemplate
import bon.jo.html.DomShell._
import bon.jo.html.HTMLDef.{$c, _}
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep.{HtmlCpnt, PrXmlId}
import bon.jo.html.{CommonHtml, HtmlRep}
import bon.jo.memo.Entities
import bon.jo.memo.Entities.{KeyWord, MemoKeywords}
import bon.jo.memo.ui.FindViewDef._
import bon.jo.memo.ui.Routing.IntPath
import bon.jo.html.SimpleView.DSelect

import bon.jo.memo.ui.ViewsDef.ProposeInput
import org.scalajs.dom.experimental.URLSearchParams
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw
import org.scalajs.dom.raw.HTMLElement

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.Node

case class MemoTemplate(user: User)(implicit ec: ExecutionContext) extends Template with XmlTemplate:


  val view = new ViewsImpl()


  val currentKeyWord: mutable.ListBuffer[KeyWord] = scala.collection.mutable.ListBuffer[KeyWord]()
  //

  def addToCurrentKW(keyWord: KeyWord, listView: Div)(implicit v: HtmlRep[KeyWord,ViewsDef.KewWordHtml]): Unit =
    currentKeyWord += keyWord
    val cpnt :ViewsDef.KewWordHtml=  keyWord.html

    listView.appendChild(cpnt.head)
    cpnt.close.$click{_ => {
      currentKeyWord -= keyWord
      listView.removeChild(cpnt.head)
    }}



  override def xml: Node = <div id="root" class="container mt-5 p-2">

  </div>


  implicit val _404: Target = Target._404
  implicit val tp: List[Any] => Option[Int] =
    case Nil => None
    case a => Some(a.head.asInstanceOf[Int])
  val pathToTarget: Routing.Path => Target = Routing.create {
    case Paths.pMemo => Target.MemoCreation
    case Paths.pFind => Target.FindMemo
    case Paths.pCreationKW => Target.KeyWordK
    case o@_ => o.matches(Paths.pMemo / IntPath) match
      case Some(int) => Target.ReadMemo(int)
      case _ => Target._404
  }

  def target: Target = pathToTarget(Routing.urlPath)



  def addMemo(value: Entities.MemoKeywords): Unit =

    val cpnt = new view.viewsDef.MKCpnt(value,proposeView)

    implicit val keyWordsBuffer: ListBuffer[KeyWord] = ListBuffer.from(value.keyWords.toList)


    val html = cpnt.get


    val div = $l div (html)
    div._class = "card"
    memosCOnr += div
    cpnt.postDelete = cpnt.postDelete :+ (() => {
      memosCOnr.remove(div)

    })

    ()

  object memosCOnr:
    def remove(div: HTMLElement): Unit =
      val  p = div.parentElement
      div.safeRm()
      cnt-=1
      if p.children.isEmpty then
        p.safeRm()

    private var cnt = 0
    private var current: HTMLElement = _

    private def div = $ref div { e => e._class = "cd card-deck pb-1" }

    def clean(): Unit =
      me.$classSelect("cd").foreach(_.removeFromDom())
      cnt = 0

    def +=(h: HTMLElement): Unit =
      if cnt % 3 == 0 then
        current = div
        me += current
      cnt += 1
      current += h


  override def init(p: HTMLElement): Unit =

    implicit val pa: HTMLElement = p
    val spi = CommonHtml.spinner
    p.appendChild(spi)
    Daos.keyWordDao.readAll().map(implicit allKeyWord => {
      proposeView.addAll(allKeyWord)
      val trPage = target match {
        case Target.MemoCreation => memoCreationLoad()
        case Target.KeyWordK =>
          p.appendChild(view.keyWordView.cpnt)
          allKeyWord.foreach { kw =>
            view.keyWordView.+=(kw)
          }
          view.addKwEvent
          Future.successful(())
        case Target.ReadMemo(id) =>
          Daos.memoKeyWord.read(id).map {
            case Some(value) =>
              addMemo(value)

            case None =>
          }
        case Target.FindMemo =>
          findMemo()
          Future.successful(())
        case Target._404 =>
          _404Load()
          Future.successful(())
      }
      trPage.onComplete {
        _ =>  p.removeChild(spi)
      }

    })




  def _404Load()(implicit p: HTMLElement) =
    p.clear()
    p.addChild[raw.HTMLHeadingElement](<h1>page non trouv?</h1>)


  import bon.jo.memo.ui.ViewsDef.KewWordHtml.keyWordWith
  private val proposeView =
    ProposeView[KeyWord,HtmlCpnt]()
  private val keyWordToHtml = ViewsDef.kwIO()

  def memoCreationLoad()(implicit p: HTMLElement, kws: Iterable[KeyWord]): Future[Unit] =
    val lView: Div = $c.div

    val memoKeywWord: MemoKeyWordViewListCreate =


      def addToCurrentKW_(keyWord: KeyWord): Unit =
        import ViewsDef.KewWordHtml.WithClose._
        addToCurrentKW(keyWord, lView)

      val propose =
        new ProposeInput[KeyWord](_.value, "Creer/Chercher tags")(
          keyWordToHtml, Daos.keyWordDao.create, proposeView,addToCurrentKW_)

      new MemoKeyWordViewListCreate(propose, lView, new MemoCtxView(None), addMemo)

    p.appendChild(memoKeywWord.cpnt);
    memoKeywWord.memoKeywWordtx.memoType.selectFirst()

    memoKeywWord.memoKeywWordtx.makeSwitchView()
    memoKeywWord.memoKeywWordtx.memoList.addEvent()




    memoKeywWord.addEventNewMemoKeyWord(currentKeyWord)
    allMemos.map {
      d =>
        d.foreach(addMemo)
    }


  //implicit val e: FindView.FindViewProvider.type = FindView.FindViewProvider
  def findMemo()(implicit p: HTMLElement, kws: Iterable[KeyWord], ec: ExecutionContext): Unit =
    p ++= FindParam("Chercher").htmlp(FindViewCtx(this, kws, ec)).list

  val searchParams = new URLSearchParams(org.scalajs.dom.window.location.search)
  val (limit, offset) = Try {
    (Option(searchParams.get("limit")).map(_.toInt).getOrElse(-1)
      , Option(searchParams.get("from")).map(_.toInt).getOrElse(-1))
  } match
    case Failure(_) => (-1, -1)
    case Success(value) => value

  def allMemos: Future[Iterable[MemoKeywords]] = Daos.memoKeyWord.readAll(limit = limit, offset = offset)






