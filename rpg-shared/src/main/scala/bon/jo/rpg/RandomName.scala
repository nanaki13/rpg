package bon.jo.rpg

import scala.util.Random

object RandomName:
  val r = new Random()
  val weapon = List("Epée", "Couteau", "Dague", "Sabre", "Shuruken", "Marteau")
  val weaponMod = List("de l'enfer", "qui tue", "de l'ombre", "de la lumière", "", "divin")
  def randomWeaponName(): String =
    weapon(r.nextInt(weapon.size))+" "+weaponMod(r.nextInt(weapon.size))

  val sylab = List("bou", "ba", "bi", "cla", "d", "ri", "dim", "me", "ro", "zi", "na", "ki", "r")

  def apply(): String =

    val nbSyl = r.nextInt(5)
    val ret = (for i <- 0 to nbSyl yield sylab(r.nextInt(sylab.size))).mkString("")
    ret.head.toUpper +: ret.tail
