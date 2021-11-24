package bon.jo.rpg.stat

import bon.jo.common.Alg


object BaseState:

  object `0` extends AnyRefBaseStat.Impl[Int](0, 0, 0, 0, 0, 0, 0, 0, 0)

  object `1` extends AnyRefBaseStat.Impl[Int](1, 1, 1, 1, 1, 1, 1, 1, 1)

  object `0f` extends AnyRefBaseStat.Impl[Float](0, 0, 0, 0, 0, 0, 0, 0, 0)

  object `1f` extends AnyRefBaseStat.Impl[Float](1, 1, 1, 1, 1, 1, 1, 1, 1)

  object `0d` extends AnyRefBaseStat.Impl[Double](0, 0, 0, 0, 0, 0, 0, 0, 0)

  object `1d` extends AnyRefBaseStat.Impl[Double](1, 1, 1, 1, 1, 1, 1, 1, 1)

  object ImplicitCommon:
    implicit val iToF: Int => Float = e => e.toFloat
    implicit val fToI: Float => Int = e => e.round
    given (AnyRefBaseStat[Int] => AnyRefBaseStat.Impl[Int]) = _.asInstanceOf[AnyRefBaseStat.Impl[Int]]
    implicit val genIntToFloat: AnyRefBaseStat[Int] => AnyRefBaseStat[Float] = AnyRefBaseStat[Float, Int](_)
    implicit val genFloatToInt: AnyRefBaseStat[Float] => AnyRefBaseStat[Int] = AnyRefBaseStat[Int, Float](_)
    //    implicit val genIntToFloatP: GenBaseState[Int] => GenBaseState[Float] = BaseStatImpl[Float, Int](_)
    //    implicit val genFloatToIntP: GenBaseState[Float] => GenBaseState[Int] = BaseStatImpl[Int, Float](_)
    implicit val algFloat: Alg[Float] = new Alg[Float] {
      override def +(a: Float, b: Float): Float = a + b

      override def -(a: Float, b: Float): Float = a - b

      override def *(a: Float, b: Float): Float = a * b

      override def /(a: Float, b: Float): Float = a / b
    }
    implicit val algInt: Alg[Int] = new Alg[Int] {
      override def +(a: Int, b: Int): Int = a + b

      override def -(a: Int, b: Int): Int = a - b

      override def *(a: Int, b: Int): Int = a * b

      override def /(a: Int, b: Int): Int = a / b
    }




