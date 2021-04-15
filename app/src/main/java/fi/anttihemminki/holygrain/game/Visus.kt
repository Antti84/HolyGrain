package fi.anttihemminki.holygrain.game

import fi.anttihemminki.holygrain.ACTIVE_ACTIVITY
import kotlin.math.round
import kotlin.math.tan

data class DistanceRange(val near: Double, val far: Double)

const val gapSizePermissibleError = 0.025

class Visus(private val decimal: Double) {
    private val pixelSizeCm: Double = 2.54 / ACTIVE_ACTIVITY.screen.ppi
    private val minArc: Double = 1.0 / decimal

    override fun toString(): String {
        return "Visus: $decimal, min_arc: $minArc"
    }

    fun getDistanceRange(distance: Double): DistanceRange {
        val tangent = tan(Math.toRadians(1.0/(60*decimal)))

        val gapSize = distance*tangent
        val pixels = round(gapSize/pixelSizeCm)
        val near = pixels*pixelSizeCm*(1.0-gapSizePermissibleError)/tangent
        val far = pixels*pixelSizeCm*(1.0+gapSizePermissibleError)/tangent
        return DistanceRange(near, far)
    }
}