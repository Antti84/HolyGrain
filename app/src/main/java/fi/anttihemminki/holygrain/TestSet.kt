package fi.anttihemminki.holygrain

enum class TestSetState(val numMeasures: Int, val hint: String) {
    EI_ALOITETTU(0, ""),
    ASETTELE_KASVO(1, "Asettele vain oma naama näkyviin"),
    VALITE_KASVO(1, "Vain oma naama näkyviin."),

    // ETÄISYYDET
    TESTI(5, "Testataan 1"),
    TESTI2(10, "Testataan 2"),
    TESTI3(15, "Testataan 3");

    // NAAMAN ERI SIJAINNIT



    // NAAMANVÄÄNTELYT YMS



    // USEITA NAAMOJA

    fun getIndex(): Int {
        for((index, s) in values().withIndex()) {
            if(s == this) return index
        }
        return -1
    }

    fun getNext(): TestSetState? {
        val i = getIndex()
        if(i == values().size - 1) return null
        return values()[i+1]
    }
}