package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.MonoEither
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono

fun <T> MonoEither<T>.resolveEither(): Mono<ServerResponse> {
    return this.flatMap {
        it.map { result ->
            ok()
                    .bodyValue(result)
        }.getOrElseGet { error ->
            status(resolveStatus(error))
                    .bodyValue(error)
        }
    }
}

private fun resolveStatus(appError: AppError): HttpStatus {
    return when(appError) {
        AppError.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        else -> HttpStatus.BAD_REQUEST
    }
}

fun onlyOwners(req: ServerRequest, action: (ServerRequest) -> Mono<ServerResponse>): Mono<ServerResponse> {

    //TODO get JWT and check is owner by role

    return action(req)
}