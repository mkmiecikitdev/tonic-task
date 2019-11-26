package kmiecik.michal.tonictask.repertoire.api

import io.vavr.collection.List

data class RepertoireDto(
        val filmId: String,
        val priceValue: Float,
        val priceCurrency: String,
        val times: List<String>
)