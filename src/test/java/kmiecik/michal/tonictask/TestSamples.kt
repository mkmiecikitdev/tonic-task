package kmiecik.michal.tonictask

import io.vavr.collection.List
import kmiecik.michal.tonictask.films.FilmExternalId
import kmiecik.michal.tonictask.films.FilmExternalIdType
import kmiecik.michal.tonictask.films.api.NewFilm

object TestSamples {

    fun sampleFilms(): List<NewFilm> {
        return List.of(
                NewFilm(name = "Film 1", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilm(name = "Film 2", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilm(name = "Film 3", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb)))
        )
    }

}
