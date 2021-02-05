package fi.anttihemminki.holygrain

enum class TestSetState(val numMeasures: Int, val hint: String) {
    EI_ALOITETTU(0, ""),

    VALITE_KASVO(1, "Vain oma naama näkyviin."),

    // ETÄISYYDET
    TESTI(5, "Testataan 1"),
    TESTI2(10, "Testataan 2"),
    TESTI3(15, "Testataan 3"),

    // NAAMAN ERI SIJAINNIT



    // NAAMANVÄÄNTELYT YMS



    // USEITA NAAMOJA
}