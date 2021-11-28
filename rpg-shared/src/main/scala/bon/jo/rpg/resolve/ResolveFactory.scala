package bon.jo.rpg.resolve

import bon.jo.rpg.resolve.PersoResolveContext.SlowResolve
import bon.jo.rpg.stat.raw.IntBaseStat
import bon.jo.rpg.stat.{GameElement, Perso}
import bon.jo.rpg.ui.PlayerUI
import bon.jo.rpg.{Affect, AffectResolver, FactorEffectt, TimedTrait}
import bon.jo.rpg.draw.draw
import scala.util.Random
import bon.jo.rpg.util.Script.*
import bon.jo.rpg.resolve.PersoResolveContext.*
import bon.jo.rpg.AffectResolver.Resolver
trait ResolveFactory(val affect :  Affect)(using formulesMap: Map[Formule.ID, Formule]):

  import Formule.given
  import bon.jo.common.give.given

 
  given Affect = affect


  def formules = affect.formuleTypes.map(t => t -> AffectResolver.read(t).getOrElse(throw new RuntimeException(s"no formules ${affect} ${t}"))).toMap
  inline def successFormule : Formule = formules(FormuleType.ChanceToSuccess)

  inline def facteurFormule : Formule= formules(FormuleType.Factor)


  inline def turnDuration: Formule = formules(FormuleType.TurnDuration)

  inline def degat: Formule = formules(FormuleType.Degat)


  def successF: ((IntBaseStat, IntBaseStat)) => Float = successFormule.formule.toFunction[(IntBaseStat, IntBaseStat)]()
  def facteurF: ((IntBaseStat, IntBaseStat)) => Float = facteurFormule.formule.toFunction[(IntBaseStat, IntBaseStat)]()
  def turnDurationF: ((IntBaseStat, IntBaseStat)) => Float = turnDuration.formule.toFunction[(IntBaseStat, IntBaseStat)]()
  type P = TimedTrait[GameElement]
  def uiMessage(att: Perso,perso: Perso, factor: FactorEffectt)(using ui: PlayerUI): Unit = 
    ui.message(s" ${att.name} fait ${factor.name.name} sur ${perso.name}",5000)

    
  def createResolve: Resolver[TimedTrait[Perso], TimedTrait[GameElement],affect.type] =
    new Resolver[TimedTrait[Perso], TimedTrait[GameElement],affect.type] :
      
      val r = Random()

      def resolveAffect(attp: TimedTrait[Perso], ciblep: P): PlayerUI.UI[P] =
        (attp.value[Perso], ciblep.value[Perso]) match
          case (att: Perso, cible: Perso) =>

            val chanceToHit = successF(att.stats, cible.stats).round
            val factor = facteurF(att.stats, cible.stats).round
            val turnDuration = turnDurationF(att.stats, cible.stats).round
            PlayerUI(s"Rèsolution de ${affect.name} provenant de ${att.name} sur ${cible.name}")
            PlayerUI(s"Chance de succés : ${(chanceToHit * 100)} %")
            r.draw(chanceToHit.toFloat,
              e => PlayerUI(s"Lancer : ${(e * 100).round.toInt} %")
              ,
              ok = {
                PlayerUI(s"Réussite!")

                val eff: FactorEffectt = FactorEffectt(turnDuration, factor.toFloat, affect)
                uiProcess(att,ciblep.addEffect(eff), eff)
              }
              ,
              ko = {
                PlayerUI(s"Echec!")
                ciblep
              }
            )


      def uiProcess(att: Perso,perso: P, factor: FactorEffectt)(using ui: PlayerUI): P =
        perso.value[Perso] match
          case p: Perso =>
            uiMessage(att,p,factor)
            ui.cpntMap(perso.id).update(Some(perso.cast))
            perso
