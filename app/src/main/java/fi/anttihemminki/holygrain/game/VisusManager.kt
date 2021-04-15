package fi.anttihemminki.holygrain.game

import android.util.Log
import fi.anttihemminki.holygrain.facedistance.TAG

data class DecimalVisus(val visus: Double)

val decimalVisusList = listOf(
        DecimalVisus(2.00),
        DecimalVisus(1.60),
        DecimalVisus(1.25),
        DecimalVisus(1.00),
        DecimalVisus(0.80),
        DecimalVisus(0.63),
        DecimalVisus(0.50),
        DecimalVisus(0.40),
        DecimalVisus(0.32),
        DecimalVisus(0.25),
        DecimalVisus(0.16),
        DecimalVisus(0.125),
        DecimalVisus(0.10),
        DecimalVisus(0.08),
        DecimalVisus(0.06),
        DecimalVisus(0.05)).reversed()

data class VisusData(val visus: DecimalVisus, val direction: Direction)

enum class FaceErrors { NO_ERRORS, TOO_FAR, TOO_CLOSE }

class VisusManager() {
    val distances = mutableMapOf<Long, Double>()

    val rows: ArrayList<VisusRow> = arrayListOf()
    init {
        for(visus in decimalVisusList) {
            rows.add(VisusRow(visus))
        }
    }
    var rowIndex = 0

    fun receiveDistance(distance: Double, time: Long) {
        distances[time] = distance
    }

    fun getCurrentVisusData(): VisusData { return rows[rowIndex].getCurrent() }
}

class VisusRow(val visus: DecimalVisus) {
    var directions: ArrayList<Direction> = arrayListOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
    var currentIndex = 0

    init {
        directions.shuffle()
        Log.i(TAG, "VisusRow ${visus.visus}, directions: $directions")
    }

    fun getCurrent(): VisusData {
        if(currentIndex < 0) currentIndex = 0
        if(currentIndex >= directions.size) currentIndex = directions.size-1

        return VisusData(
                visus,
                directions[currentIndex]
        )
    }
}