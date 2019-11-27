package kmiecik.michal.tonictask.repertoire.api

import io.vavr.collection.List

data class RepertoireDataDto(
        val filmId: String,
        val priceValue: String,
        val priceCurrency: String,
        val times: List<String>
)
