package fi.anttihemminki.holygrain

import fi.anttihemminki.holygrain.facedistance.Eyes
import kotlin.math.abs

val distances = arrayOf(30, 25, 35, 40, 45)
val eyes = arrayOf(Eyes.OA, Eyes.OD, Eyes.OS)
//val covers = arrayOf("closed", "covered")

class TestSetState() {
    fun getCurrentState(): TestSetStateData {
        return states[currentState]
    }

    fun goToNextState(): Boolean {
        currentState++
        return currentState < states.size
    }

    var currentState = 0
    val states: Array<TestSetStateData>

    init {
        val stateList = ArrayList<TestSetStateData>()

        for(distance in distances) {
            for(eye in eyes) {
                //for(cover in covers) {

                    //if(eye == Eyes.OA) {
                    //    if(cover == covers[0]) {
                    //        val item = TestSetStateData( distance, eye, "open")
                    //        stateList.add(item)
                    //    }
                    //} else {
                    val item = TestSetStateData( distance, eye)
                    stateList.add(item)
                    //}
                //}
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
        //val cover: String,
)

fun testSetStateDataToPositionString(state: TestSetStateData): String {

    val coverString = if(state.eye == Eyes.OA) {
        "Molemmat silmät auki"
    } else {
        if(state.eye == Eyes.OD) "Peitä vasen silmä" else "Peitä oikea silmä"
        /*if(state.cover == "closed") {
            "Sulje $coveredEyeStr silmä"
        } else {
            "Peitä $coveredEyeStr silmä kämmenellä"
        }*/
    }

    return "Etäisyys: ${state.distance}cm\n$coverString"
}