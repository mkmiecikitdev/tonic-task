package kmiecik.michal.tonictask.repertoire

import reactor.core.publisher.Mono

interface RepertoireRepository {

    fun save(repertoire: Repertoire): Mono<Repertoire>

    fun findByFilmId(filmId: String): Mono<Repertoire>

}