package kmiecik.michal.tonictask.kernel

import io.vavr.collection.List
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import reactor.core.publisher.Mono

typealias MonoEither<T> = Mono<Either<AppError, T>>

typealias MonoList<T> = Mono<List<T>>