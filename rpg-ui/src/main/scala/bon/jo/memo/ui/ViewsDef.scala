package bon.jo.memo.ui

import bon.jo.dao.Dao.FB
import bon.jo.html.DomShell.{ExtendedElement, ExtendedHTMLCollection}
import bon.jo.html.HTMLDef._
import bon.jo.html.HtmlEventDef.ExH
import bon.jo.html.HtmlRep.{Deletable, HtmlCpnt, HtmlRepParam, ListRep, PrXmlId}
import bon.jo.html.{CommonHtml, HtmlRep}
import bon.jo.memo.Entities.{KeyWord, Memo, MemoKeywords, MemoType}
import bon.jo.memo.ui.MemoLists.MemoListJS
import bon.jo.memo.ui.ViewsDef.KewWordHtml.WithClose.keyWordWithClose
import bon.jo.memo.ui.ViewsDef.ProposeInput
import bon.jo.ui.UpdatableCpnt
import org.scalajs.dom.console
import org.scalajs.dom.html.{Anchor, Button, Div, Input}
import org.scalajs.dom.raw.{Element, HTMLElement}
import bon.jo.html.PopUp
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}

object ViewsDef:
  val kwClass = "kwClass"

  def apply(): ViewsDef = new ViewsDef

  def kwIO() = new IOHtml[Input, KeyWord]($c.input: Input, input => KeyWord(None, input.value))

  var events: Option[Ev] = None

  class ProposeInput[A](strF: A => String, textCreer: String)(

    override val ioHtml: IOHtml[Input, A]
    , save: A => Future[Option[A]],
    proposeView: ProposeView[A, _], sel: A => Unit
  )(implicit executionContext: ExecutionContext)
    extends Propose[A, Input](ioHtml, proposeView):
    btn.textContent = textCreer
    ioHtml.html.$keyup {
      v =>
        focus()
        proposeView.doFilter(ioHtml.html.value.trim.nonEmpty && strF(_).toLowerCase.contains(ioHtml.html.value.toLowerCase))
        events.foreach {
          e =>
            e.foreach { rlrv =>
              rlrv._1.removeEventListener("click", rlrv._2)
            }
        }
        events = Some(createAllEvent(sel))

    }

    btn.$spinner {
      ()=>
        save(ioHtml.toValue).recover{case _ => None}.map {
          case None => PopUp("Marche pas...")
          case Some(value) => {
            val els = proposeView.+=(value)
            clickEvent(els.head, value)(sel)
            PopUp("Ok")
          }
        }

    }





  val svgNs = "http://www.w3.org/2000/svg"

  def help: Element =

    (
      $refns.svg(svgNs, { (svg: Element) =>
        svg $attr List("width" -> "16", "height" -> "16", "fill" -> "currentColor",
          "class" -> "bi bi-question-circle", "viewBox" -> "0 0 16 16") ++= (
          $attrns.path(svgNs, List( "d" -> "M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"))
          , $attrns.path(svgNs, List("d" -> "M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286zm1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94z"))
        )
      }))

  trait KewWordHtml extends HtmlCpnt:
    val close = CommonHtml.closeBtn

    def kw(): KeyWord

    override val get: IterableOnce[HTMLElement] =
      val ret = KewWordHtml.keyWordWith.html(kw())
      val html = ret.get
      html.iterator.toList.head += close
      html

  object KewWordHtml:
    object WithClose:
      implicit val keyWordWithClose: HtmlRep[KeyWord, KewWordHtml] = (memo: KeyWord) =>
        () => memo

    implicit val keyWordWith: HtmlRep[KeyWord, HtmlCpnt] = (kw: KeyWord) => {
      HtmlCpnt {
        () => {
          val ret = $t span {
            kw.value
          }
          ret._class = s"badge badge-primary m-1 ${ViewsDef.kwClass}"
          Some(ret)
        }
      }
    }

class ViewsDef():


  class MemoCpnt(val memo: Memo, val mList: Option[MemoListView]) extends HtmlCpnt with UpdatableCpnt[Memo]:
    def lienTilre_(memo: Memo) = $ref a {
      lienTilre =>
        lienTilre._class = "a-title"
        lienTilre.textContent = memo.title
        lienTilre.asInstanceOf[Anchor].href = s"/app/memo/${memo.id.getOrElse(0)}"
    }

    val lienTilre = lienTilre_(memo)

    def content_(memol: Memo): HTMLElement =
      $ref div {
        cnt =>
          cnt._class = "m-content"

          memol.memoType match
            case MemoType.Text => cnt.innerHTML = memol.content.replaceAll("""#\[([^|]*)\|([^\s]*)]""", """<a href="$2">$1</a>""")
            case MemoType.Json =>
              Try {
                mList.foreach(ml => {
                  ml.data = JSON.parse(memol.content).asInstanceOf[MemoListJS]

                  cnt += ml.html
                  ml.addEvent()
                })

                mList
              } match
                case Failure(a) => s"Erreur en traitant : ${memo.content}\n$a"
                case Success(value) => value.toString()
      }

    val content = content_(memo)

    def textType(memop: Memo) =
      s"""Type : ${
        memop.memoType match {
          case MemoType.Text => "Text"
          case MemoType.Json => "List"
        }
      }"""

    val _tpeDiv = ($t div
      textType(memo))

    val body = $ref div {
      bdy =>
        bdy._class = "card-body"
        bdy ++= (
          $ref div {
            tpeDiv =>
              tpeDiv._class = "m-type"
              tpeDiv += _tpeDiv

          },
          content
        )
    }
    val title = $ref div { h1Title =>
      h1Title += (lienTilre)
      h1Title._class = "card-title"
    }
    val html = List(title
      , body)

    override def get: IterableOnce[HTMLElement] = html


    override def update(value: Option[Memo]): Unit =

      value foreach {
        m =>

          lienTilre.textContent = m.title
          _tpeDiv.innerText = textType(m)
          content.innerHTML = ""
          val c = content_(m)

          // content.innerHTML = c.innerHTML
          c.childNodes.foreach {
            p =>
              if !scalajs.js.isUndefined(p) then
               
                content.appendChild(p)

          }
      }

  implicit val memoXml: HtmlRepParam[Memo, MemoListView, MemoCpnt] =
    (memo, mList) => new MemoCpnt(memo, mList)


  class MKCpnt(memoInitial: MemoKeywords, proposeView: ProposeView[KeyWord, HtmlCpnt])(implicit val executionContext: ExecutionContext) extends HtmlCpnt with UpdatableCpnt[MemoKeywords] with Deletable:

    val ctx = new MemoCtxView(Some(memoInitial))

    val kwDiv: Div = $l.t div memoInitial.keyWords.html.flatMap(_.list)

    val saveButton: Button = $ref.t button {
      (save: Button) =>
        save.textContent = "save"
        save._class = "btn-save btn btn-primary"
    }
    val editButton: Button = $ref.t button {
      (save: Button) =>
        save.textContent = "edit"
        save._class = "edit btn btn-primary"
    }
    val propose = new ProposeInput[KeyWord](_.value, "Creer/Chercher tags")(
      ViewsDef.kwIO(), Daos.keyWordDao.create, proposeView, addKeyWord)
    val footer: HTMLElement = $ref div {
      ff =>
        ff._class = "card-footer"
        ff ++= ($t h3 "tags", kwDiv, propose.html)
    }
    val cpnt: MemoCpnt = memoInitial.memo.htmlp(Some(ctx.memoList))
    deleteButton.style.cssFloat = "right"
    cpnt.title.appendChild(deleteButton)
    cpnt.body ++= (saveButton, editButton)
    val l: List[HTMLElement] = cpnt.list :+ footer
    implicit val keyWordsBuffer: ListBuffer[KeyWord] = ListBuffer.from(memoInitial.keyWords.toList)


    override def get: List[HTMLElement] = l


    def addKeyWord(selected: KeyWord): Unit =
      keyWordsBuffer += selected
      val htmlKW = selected.html.list
      kwDiv ++= htmlKW
      deleteEvent(selected, htmlKW.head)


    def deleteEvent(kw: KeyWord, htmlKw: HTMLElement)(implicit lubber: ListBuffer[KeyWord]): Unit =

      htmlKw.$classSelect(CommonHtml.closeClass).foreach { btnClose =>
        btnClose.asInstanceOf[HTMLElement].$click {
          _ =>
            lubber -= kw
            htmlKw.removeFromDom()
        }
      }



    override def update(value: Option[MemoKeywords]): Unit =
      cpnt.update(value.map(_.memo))


    def save(): Future[Unit] =

      ctx.currentMemoOption = ctx.currentMemoOption.map { currentMemo =>
        currentMemo.copy(memo = ctx.newMemo.copy(id = currentMemo.memo.id), keyWords = keyWordsBuffer.toSet)
      }
      ctx.currentMemoOption.map { currentMemo =>
        Daos.memoKeyWord.update(currentMemo).map { value =>
          PopUp("Sauvegarde OK")
          //  ctx.memoList.html.safeRm()
          ctx.memoType.safeRm()
          ctx.tInput.safeRm()
          ctx.contentInput.safeRm()
          update(value)
        } recover {
          case exception => console.log(exception); PopUp("Sauvegarde KO")
        }
      } getOrElse(Future.failed(new IllegalStateException("")))


    keyWordsBuffer zip kwDiv.$classSelect(ViewsDef.kwClass).map(_.asInstanceOf[HTMLElement]) foreach {
      deleteEvent _ tupled _
    }



    saveButton.$spinner(save)


    editButton.$click { _ =>
      ctx.initialInput()
      l.foreach(_.$classSelect.`a-title`.map(_.asInstanceOf[HTMLElement]).foreach { a =>
        a.parentElement += ctx.tInput

      })
      l.foreach(_.$classSelect.`m-content`.map(_.asInstanceOf[HTMLElement]).foreach { a =>

        a.parentElement.insertBefore(ctx.contentInput, saveButton)
        a.parentElement.insertBefore(ctx.memoList.html, saveButton)


        ()
      })
      l.foreach(_.$classSelect.`m-type`.map(_.asInstanceOf[HTMLElement]).foreach { a =>
        a += (ctx.memoType)

      })
      ctx.makeSwitchView()
      ctx.memoList.addEvent()
    }

    override def delete(): FB = Daos.memoKeyWord.delete(memoInitial.memo.id.get)








