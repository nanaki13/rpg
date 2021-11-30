package bon.jo.rpg.ui.page

import bon.jo.rpg.ui.Export

enum RpgPage(val id : Id ,   label : String) extends Page[Id] :
  case Simulation(  label : String) extends RpgPage(Id.Simulation,label)
  case EditArme(  label : String) extends RpgPage(Id.EditArme,label)
  case EditPerso(  label : String) extends RpgPage(Id.EditPerso,label)
  case Export(  label : String) extends RpgPage(Id.Export,label)
  case Import(  label : String) extends RpgPage(Id.Import,label)
  case EditFormule(  label : String) extends RpgPage(Id.EditFormule,label)
  case News(  label : String) extends RpgPage(Id.News,label)
  
enum Id:
  case Simulation
  case EditArme
  case EditPerso
  case Export
  case Import
  case EditFormule
  case News


  /** "éditer/créer Arme" -> initChoixArme,

      "éditer/créer Perso" -> initChoixPerso,
      "Simulation" -> (() => 
        org.scalajs.dom.window.location.search = "page=simulation"
        simulation()
        ),
      "Export" -> exportF,"Import" -> importDataPopUp,
      "Test Formule" -> editPage.editPage(using root),
      "Edit Formule" -> (() => EditFormauleAffect.simulation),
      "News" -> (() =>**/