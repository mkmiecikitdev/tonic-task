package kmiecik.michal.tonictask.films

import io.vavr.collection.List
import kmiecik.michal.tonictask.films.api.FilmDto

data class FilmData(
        val id: String,
        val filmName: FilmName,
        val externalIdList: List<FilmExternalId>
) {

    fun toDto() = FilmDto(id, name = filmName.name, externalIds = externalIdList)

}