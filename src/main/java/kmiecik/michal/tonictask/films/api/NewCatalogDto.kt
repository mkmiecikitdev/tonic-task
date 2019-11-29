package kmiecik.michal.tonictask.films.api

import io.vavr.collection.List

data class NewCatalogDto(val films: List<NewFilmDto>)
