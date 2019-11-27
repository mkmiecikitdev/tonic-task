package kmiecik.michal.tonictask.ratings

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RatingsRepository {

    fun save(ratings: Ratings): Mono<Ratings>

    fun findByFilmId(filmId: String): Mono<Ratings>

    fun findAll(): Flux<Ratings>

}