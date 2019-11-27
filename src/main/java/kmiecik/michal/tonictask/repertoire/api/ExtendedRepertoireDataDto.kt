package kmiecik.michal.tonictask.repertoire.api

import io.vavr.collection.List

data class ExtendedRepertoireDataDto (
        val filmId: String,
        val filmName: String,
        val priceValue: String,
        val priceCurrency: String,
        val times: List<String>
)
