package bon.jo.rpg.resolve

import bon.jo.rpg.resolve.PersoResolveContext.SlowResolve
import bon.jo.rpg.stat.raw.IntBaseStat
import bon.jo.rpg.stat.{GameElement, Perso}
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.{Affect, AffectResolver, FactorEffectt, TimedTrait}
import bon.jo.rpg.draw.draw
import scala.util.Random
import bon.jo.memo.Script.*
import bon.jo.rpg.resolve.PersoResolveContext.*
trait PersoSlowPersoFactory(using formulesMap: Map[Formule.ID, Formule]):

  import Formule.given
  import bon.jo.common.give.given

  given Affect = Affect.Slow


  inline def successFormule = AffectResolver.read(FormuleType.ChanceToSuccess)

  inline def facteurFormule = AffectResolver.read(FormuleType.Factor)

  inline def turnDuration = AffectResolver.read(FormuleType.TurnDuration)

  val successF: ((IntBaseStat, IntBaseStat)) => Float = successFormule.get.formule.toFunction[(IntBaseStat, IntBaseStat)]()
  val facteurF: ((IntBaseStat, IntBaseStat)) => Float = facteurFormule.get.formule.toFunction[(IntBaseStat, IntBaseStat)]()
  val turnDurationF: ((IntBaseStat, IntBaseStat)) => Float = turnDuration.get.formule.toFunction[(IntBaseStat, IntBaseStat)]()

  def createResolve: SlowResolve =
    new SlowResolve :
      type P = TimedTrait[GameElement]
      val r = Random()

      def resolveAffect(attp: TimedTrait[Perso], ciblep: P): PlayerUI.UI[P] =
        (attp.value[Perso], ciblep.value[Perso]) match
          case (att: Perso, cible: Perso) =>

            val chanceToHit = successF(att.stats, cible.stats).round
            val factor = facteurF(att.stats, cible.stats).round
            val turnDuration = turnDurationF(att.stats, cible.stats).round
            PlayerUI(s"Chance de succés : ${(chanceToHit * 100)} %")
            r.draw(chanceToHit.toFloat,
              e => PlayerUI(s"Lancer : ${(e * 100).round.toInt} %")
              ,
              ok = {
                PlayerUI(s"Réussite!")

                val eff: FactorEffectt = FactorEffectt(3, factor.toFloat, Affect.Slow)
                uiProcess(ciblep.addEffect(eff), eff)
              }
              ,
              ko = {
                PlayerUI(s"Echec!")
                ciblep
              }
            )


      def uiProcess(perso: P, factor: FactorEffectt)(using ui: PlayerUI): P =
        perso.value[Perso] match
          case p: Perso =>
            ui.message(s"${p.name} est ralenti de ${factor.factor} dans le temps pedans ${factor.time} tour", 5000)
            ui.cpntMap(perso.id).update(Some(perso.cast))
            perso
