package kmiecik.michal.tonictask.films.api

import io.vavr.collection.List
import kmiecik.michal.tonictask.films.FilmExternalId

data class NewFilmDto(val name: String, val externalIds: List<FilmExternalId>)
