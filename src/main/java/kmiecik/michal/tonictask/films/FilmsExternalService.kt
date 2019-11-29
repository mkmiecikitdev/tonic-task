package kmiecik.michal.tonictask.films

import kmiecik.michal.tonictask.films.api.FilmDetailsDto
import reactor.core.publisher.Mono

interface FilmsExternalService {

    fun fetchDetails(externalId: String): Mono<FilmDetailsDto>

}