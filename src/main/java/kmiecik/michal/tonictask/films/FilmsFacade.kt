package kmiecik.michal.tonictask.films

import io.vavr.collection.List
import kmiecik.michal.tonictask.films.api.FilmDto
import kmiecik.michal.tonictask.films.api.NewCatalogDto
import kmiecik.michal.tonictask.films.api.NewFilmDto
import reactor.core.publisher.Mono

class FilmsFacade(
        private val filmsRepository: FilmsRepository,
        private val filmFactory: FilmFactory = FilmFactory()) {

    fun createFilmCatalog(newCatalog: NewCatalogDto): Mono<List<FilmDto>> {
        return filmsRepository.countAll()
                .flatMap { count ->
                    if (count > 0) listFilms()
                    else saveNewFilms(newCatalog.films)
                }
    }

    fun listFilms(): Mono<List<FilmDto>> {
        return filmsRepository.findAll()
                .map { it.toDto() }
                .collectList()
                .map { List.ofAll(it) }
    }

    private fun saveNewFilms(newFilms: List<NewFilmDto>): Mono<List<FilmDto>> {
        return newFilms.map { filmFactory.create(it) }
                .let { filmsRepository.saveAll(it) }
                .map { it.toDto() }
                .collectList()
                .map { List.ofAll(it) }

    }

}
