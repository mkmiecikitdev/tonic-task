package kmiecik.michal.tonictask.errors

import io.vavr.control.Either
import reactor.core.publisher.Mono

enum class AppError {

    UNAUTHORIZED,
    CANNOT_PARSE_DATE,
    CANNOT_PARSE_CURRENCY,
    CANNOT_PARSE_PRICE,
    INVALID_RATING,
    LOGIN_EXISTS;

    fun <T> toEither() = Either.left<AppError, T>(this)

    fun <T> toMono() = Mono.just(toEither<T>())

}

fun <T> Either<T, T>.merge() = this.getOrElseGet { it }





