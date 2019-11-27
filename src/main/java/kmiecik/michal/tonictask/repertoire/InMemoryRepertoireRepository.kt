package kmiecik.michal.tonictask.repertoire

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryRepertoireRepository(private var repertoires: AtomicReference<Map<String, Repertoire>> = AtomicReference(HashMap.empty())) : RepertoireRepository {

    override fun findAll(): Flux<Repertoire> {
        return Flux.fromStream(repertoires.get()
                .values()
                .toJavaStream())
    }

    override fun save(repertoire: Repertoire): Mono<Repertoire> {
        repertoires.updateAndGet { it.put(repertoire.filmId, repertoire) }
        return Mono.just(repertoire)
    }

    override fun findByFilmId(filmId: String): Mono<Repertoire> {
        return repertoires.get()[filmId]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }

    }
}
