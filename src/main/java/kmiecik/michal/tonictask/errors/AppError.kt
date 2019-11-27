package kmiecik.michal.tonictask.errors

import io.vavr.control.Either
import reactor.core.publisher.Mono

enum class AppError {

    CANNOT_PARSE_DATE,
    CANNOT_PARSE_CURRENCY,
    CANNOT_PARSE_PRICE;

    fun <T> toEither() = Either.left<AppError, T>(this)

    fun <T> toMono() = Mono.just(toEither<T>())

}

//fun <T> Either<AppError, T>.merge() = this.mapLeft { it.toMono<T>() }
//        .getOrElseGet { it -> it }
//
