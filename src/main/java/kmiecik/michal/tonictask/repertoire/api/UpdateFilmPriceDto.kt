package kmiecik.michal.tonictask.repertoire.api

data class UpdateFilmPriceDto(val filmId: String, val price: String, val currency: String)