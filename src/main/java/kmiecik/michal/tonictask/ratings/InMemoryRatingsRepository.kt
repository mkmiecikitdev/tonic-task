package kmiecik.michal.tonictask.ratings

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryRatingsRepository(private var ratingsMap: AtomicReference<Map<String, Ratings>> = AtomicReference(HashMap.empty())) : RatingsRepository {

    override fun findByFilmId(id: String): Mono<Ratings> {
        return ratingsMap.get()[id]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun save(ratings: Ratings): Mono<Ratings> {
        ratingsMap.updateAndGet { it.put(ratings.filmId, ratings) }
        return Mono.just(ratings)
    }

    override fun findAll(): Flux<Ratings> {
        return Flux.fromStream(ratingsMap.get()
                .values()
                .toJavaStream())
    }
}