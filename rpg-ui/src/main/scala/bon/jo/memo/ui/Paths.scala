package bon.jo.memo.ui

import bon.jo.memo.BaseRoute

object Paths :

  import Routing._
  val appPath: Path = "app".p
  val pMemo: Path = appPath / BaseRoute.memoRoute
  val pFind: Path = pMemo / BaseRoute.find
  val pCreationKW: Path = appPath / BaseRoute.keywordRoute
