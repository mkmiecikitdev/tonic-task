package kmiecik.michal.tonictask

import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import reactor.core.publisher.Mono
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object TestUtils {

    fun <T> valueOfMono(mono: Mono<T>) = mono.block()

    fun <R> assertMonoEitherRight(mono: Mono<Either<AppError, R>>, asserts: (r: R) -> Unit) {
        assertNotNull(mono)
        val either = mono.block()
        assertTrue { either!!.isRight }
        asserts(either!!.get())
    }

    fun <R> assertMonoEitherLeft(mono: Mono<Either<AppError, R>>, asserts: (appError: AppError) -> Unit) {
        assertNotNull(mono)
        val either = mono.block()
        assertTrue { either!!.isLeft }
        asserts(either!!.left)
    }

}
