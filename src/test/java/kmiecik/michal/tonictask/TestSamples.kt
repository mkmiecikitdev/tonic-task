package kmiecik.michal.tonictask

import io.vavr.collection.List
import kmiecik.michal.tonictask.films.FilmExternalId
import kmiecik.michal.tonictask.films.FilmExternalIdType
import kmiecik.michal.tonictask.films.api.NewCatalogDto
import kmiecik.michal.tonictask.films.api.NewFilmDto

object TestSamples {

    fun sampleCatalog(): NewCatalogDto {
        return List.of(
                NewFilmDto(name = "Film 1", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilmDto(name = "Film 2", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilmDto(name = "Film 3", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb)))
        ).let {
            NewCatalogDto(it)
        }
    }

}
