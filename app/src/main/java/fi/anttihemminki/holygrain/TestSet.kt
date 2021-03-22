package fi.anttihemminki.holygrain

import kotlin.math.abs

enum class Eyes(val value: Int) {
    OA(0),
    OD(1),
    OS(2)
}

val distances = arrayOf(25, 30, 35, 40, 45)
val eyes = arrayOf(Eyes.OA, Eyes.OD, Eyes.OS)
val covers = arrayOf("closed", "covered")
val headDirectionsHoriz = arrayOf(0, 10, -10)
val headDirectionsVertic = arrayOf(0, 10, -10)

class TestSetState() {

    var currentState = 0
    val states: Array<TestSetStateData>

    init {
        val stateList = ArrayList<TestSetStateData>()

        for(distance in distances) {
            for(eye in eyes) {
                for(cover in covers) {
                    for(dirHoriz in headDirectionsHoriz) {
                        for(dirVert in headDirectionsVertic) {

                            if(distance in arrayOf(25, 35, 40, 45) && dirHoriz != 0 && dirVert != 0) continue

                            if(eye == Eyes.OA) {
                                if(cover == covers[0]) {
                                    val item = TestSetStateData( distance, eye, "open", dirHoriz, dirVert)
                                    stateList.add(item)
                                }
                            } else {
                                val item = TestSetStateData( distance, eye, cover, dirHoriz, dirVert)
                                stateList.add(item)
                            }
                        }
                    }
                }
            }
        }

        states = stateList.toTypedArray()
    }

    //fun getIndex(): Int { }

    //fun getNext(): TestSetState? { }
}

data class TestSetStateData(
        val distance: Int,
        val eye: Eyes,
        val cover: String,
        val horizDir: Int,
        val vertDir: Int
)

fun testSetStateDataToPositionString(state: TestSetStateData): String {

    val coverString = if(state.eye == Eyes.OA) {
        "Molemmat silmät auki"
    } else {
        val coveredEyeStr = if(state.eye == Eyes.OD) "vasen" else "oikea"
        if(state.cover == "closed") {
            "Sulje $coveredEyeStr silmä"
        } else {
            "Peitä $coveredEyeStr silmä kämmenellä"
        }
    }

    val headTurnHorizStr = if(state.horizDir == 0) "Pää suorassa" else {
        val dirStr = if(state.horizDir < 0) "vasemmalle" else "oikealle"
        "Käännä päätä noin ${abs(state.horizDir)} astetta ${dirStr}"
    }

    val headTurnVertStr = if(state.vertDir == 0) "Pää pystysuorassa" else {
        val dirStr = if(state.vertDir < 0) "alaspäin" else "ylöspäin"
        "Käännä päätä noin ${abs(state.vertDir)} astetta ${dirStr}"
    }

    return "Etäisyys: ${state.distance}cm\n$coverString\n${headTurnHorizStr}\n${headTurnVertStr}"
}