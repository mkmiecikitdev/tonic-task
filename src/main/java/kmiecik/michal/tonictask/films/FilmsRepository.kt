package kmiecik.michal.tonictask.films

import io.vavr.collection.List
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FilmsRepository {

    fun findAll(): Flux<FilmData>

    fun findById(id: String): Mono<FilmData>

    fun countAll(): Mono<Int>

    fun saveAll(list: List<FilmData>): Flux<FilmData>

}