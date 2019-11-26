package kmiecik.michal.tonictask

import reactor.core.publisher.Mono

object TestUtils {

    fun <T> valueOfMono(mono: Mono<T>) = mono.block()

}