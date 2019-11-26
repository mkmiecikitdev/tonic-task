package kmiecik.michal.tonictask.films

import kmiecik.michal.tonictask.films.api.NewFilm
import java.util.*

class FilmFactory {

    fun create(newFilm: NewFilm): FilmData {
        return FilmData(
                id = UUID.randomUUID().toString(),
                filmName = FilmName(newFilm.name),
                externalIdList = newFilm.externalIds)
    }

}