package kmiecik.michal.tonictask.films

import io.vavr.collection.List
import io.vavr.control.Option
import kmiecik.michal.tonictask.films.api.FilmDto

data class FilmData(
        val id: String,
        private val filmName: FilmName,
        private val externalIdList: List<FilmExternalId>
) {

    fun toDto() = FilmDto(id, name = filmName.name, externalIds = externalIdList)

    fun getExternalId(externalIdType: FilmExternalIdType): Option<String> {
        return externalIdList.find { it.type == externalIdType }
                .map { it.id }
    }

}