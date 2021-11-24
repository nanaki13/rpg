package bon.jo.rpg
package draw:
    extension (r : scala.util.Random) 
        inline def draw[R](success  : Float,pres :Float => Unit ,ok :  => R,ko :  => R ) : R =
            val d = r.nextFloat
            pres(d)
            if d  <= success then ok else ko
