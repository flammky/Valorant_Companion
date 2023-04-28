package dev.flammky.valorantcompanion.live

fun pingStrengthInRangeOf4(pingMs: Int): Int {
    require(pingMs >= 0)
    return when(pingMs) {
        in 0..45 -> 4
        in 46..70 -> 3
        in 71 .. 100 -> 2
        else -> 1
    }
}