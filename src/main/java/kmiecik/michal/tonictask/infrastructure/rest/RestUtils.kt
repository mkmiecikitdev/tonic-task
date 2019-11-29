package kmiecik.michal.tonictask.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.users.api.UserDataDto
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

const val AUTH_HEADER_KEY = "Authorization"
const val BEARER = "Bearer"

fun MonoEither<UserDataDto>.resolveEitherWithAuth(objectMapper: ObjectMapper, jwtMapper: (UserDataDto) -> String): Mono<ServerResponse> {
    return this.flatMap {
        it.map { result ->
            ok()
                    .header(AUTH_HEADER_KEY, "$BEARER ${jwtMapper(result)}")
                    .bodyValue(objectMapper.convertValue(result as Any))
        }.getOrElseGet { error ->
            status(resolveStatus(error))
                    .bodyValue(error)
        }
    }
}

private fun resolveStatus(appError: AppError): HttpStatus {
    return when (appError) {
        AppError.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        else -> HttpStatus.BAD_REQUEST
    }
}

