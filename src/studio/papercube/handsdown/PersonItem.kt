package studio.papercube.handsdown

data class PersonItem(
        override val name: String,
        override val weight: Double
) : Named, Weighed {
    companion object {
        fun parse(string: String): PersonItem {
            val split = string.split(",")
            val weight = split.getOrNull(1)?.toDoubleOrNull() ?: 1.0
            return PersonItem(split[0].trim(), weight)
        }
    }
}