package kmiecik.michal.tonictask.films

import kmiecik.michal.tonictask.films.api.NewFilmDto
import java.util.*

class FilmFactory {

    fun create(newFilm: NewFilmDto): FilmData {
        return FilmData(
                id = UUID.randomUUID().toString(),
                filmName = FilmName(newFilm.name),
                externalIdList = newFilm.externalIds)
    }

}
