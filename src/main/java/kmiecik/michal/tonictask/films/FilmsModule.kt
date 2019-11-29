package kmiecik.michal.tonictask.films

import kmiecik.michal.tonictask.films.api.FilmDetailsDto
import reactor.core.publisher.Mono

class FilmsModule {

    fun createInMemoryFacade(externalService: FilmsExternalService = FakeExternalFilmService()): FilmsFacade { // TODO BETTER
        return FilmsFacade(filmsRepository = InMemoryFilmRepository(), externalService = externalService)
    }

    private class FakeExternalFilmService : FilmsExternalService {
        override fun fetchDetails(externalId: String): Mono<FilmDetailsDto> {
            return Mono.empty()
        }
    }


}