package bon.jo.rpg.resolve

enum FormuleType:
    case Degat
    case ChanceToSuccess
    case Factor
    case TurnDuration

object FormuleType:
    def withoutDegat(): List[FormuleType] = FormuleType.values.filter(_ != FormuleType.Degat).toList