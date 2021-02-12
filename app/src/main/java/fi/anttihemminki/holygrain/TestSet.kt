package fi.anttihemminki.holygrain

enum class TestSetState(val numMeasures: Int, val hint: String) {
    EI_ALOITETTU(0, ""),
    ASETTELE_KASVO(1, "Asettele vain oma naama näkyviin"),
    VALITE_KASVO(1, "Vain oma naama näkyviin."),

    // ETÄISYYDET

    // 25CM
    OA_SUORA_25(15, "Suorassa. Etäisyys 25cm. Silmät auki."),
    OD_SUORA_25(15, "Suorassa. Etäisyys 25cm. Sulje vasen."),
    OS_SUORA_25(15, "Suorassa. Etäisyys 25cm. Sulje oikea."),
    OD_PEITTO_25(15, "Suorassa. Etäisyys 25cm. Peitä vasen."),
    OS_PEITTO_25(15, "Suorassa. Etäisyys 25cm. Peitä oikea."),

    // 30CM
    OA_SUORA_30(15, "Suorassa. Etäisyys 30cm. Silmät auki."),
    OD_SUORA_30(15, "Suorassa. Etäisyys 30cm. Sulje vasen."),
    OS_SUORA_30(15, "Suorassa. Etäisyys 30cm. Sulje oikea."),
    OD_PEITTO_30(15, "Suorassa. Etäisyys 30cm. Peitä vasen."),
    OS_PEITTO_30(15, "Suorassa. Etäisyys 30cm. Peitä oikea."),

    // 35CM
    OA_SUORA_35(15, "Suorassa. Etäisyys 35cm. Silmät auki."),
    OD_SUORA_35(15, "Suorassa. Etäisyys 35cm. Sulje vasen."),
    OS_SUORA_35(15, "Suorassa. Etäisyys 35cm. Sulje oikea."),
    OD_PEITTO_35(15, "Suorassa. Etäisyys 35cm. Peitä vasen."),
    OS_PEITTO_35(15, "Suorassa. Etäisyys 35cm. Peitä oikea."),

    // 35CM LISÄKKEET

    NAAMANVAANTELY(30, "Suorassa. 35cm. Vääntele naamaa :)."),
    KAANTO_VASEN(15, "35cm. Käännä pää noin 30 astetta vasemmalla."),
    KAANTO_OIKEA(15, "35cm. Käännä pää noin 30 astetta oikealle."),
    KAANTO_YLOS(15, "35cm. Käännä pää noin 20 astetta ylös."),
    KAANTO_ALAS(15, "35cm. Käännä pää noin 20 astetta alas."),

    // 40CM
    OA_SUORA_40(15, "Suorassa. Etäisyys 40cm. Silmät auki."),
    OD_SUORA_40(15, "Suorassa. Etäisyys 40cm. Sulje vasen."),
    OS_SUORA_40(15, "Suorassa. Etäisyys 40cm. Sulje oikea."),
    OD_PEITTO_40(15, "Suorassa. Etäisyys 40cm. Peitä vasen."),
    OS_PEITTO_40(15, "Suorassa. Etäisyys 40cm. Peitä oikea."),

    // 45CM
    OA_SUORA_45(15, "Suorassa. Etäisyys 45cm. Silmät auki."),
    OD_SUORA_45(15, "Suorassa. Etäisyys 45cm. Sulje vasen."),
    OS_SUORA_45(15, "Suorassa. Etäisyys 45cm. Sulje oikea."),
    OD_PEITTO_45(15, "Suorassa. Etäisyys 45cm. Peitä vasen."),
    OS_PEITTO_45(15, "Suorassa. Etäisyys 45cm. Peitä oikea.");

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