package kmiecik.michal.tonictask.films


import io.vavr.collection.HashMap
import io.vavr.collection.List
import io.vavr.collection.Map
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryFilmRepository(private var films: AtomicReference<Map<String, FilmData>> = AtomicReference(HashMap.empty())) : FilmsRepository {

    override fun findById(id: String): Mono<FilmData> {
        return films.get()[id]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun countAll(): Mono<Int> {
        return Mono.just(films.get().size())
    }

    override fun saveAll(list: List<FilmData>): Flux<FilmData> {
        list.forEach { filmData ->
            films.updateAndGet { it.put(filmData.id, filmData) }
        }

        return Flux.fromStream { list.toJavaStream() }
    }

    override fun findAll(): Flux<FilmData> {
        return Flux.fromStream(films.get()
                .values()
                .toJavaStream())
    }


}
