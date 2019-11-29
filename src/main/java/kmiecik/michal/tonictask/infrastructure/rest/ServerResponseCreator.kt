package kmiecik.michal.tonictask.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.infrastructure.rest.Constants.AUTH_HEADER_KEY
import kmiecik.michal.tonictask.infrastructure.rest.Constants.BEARER
import kmiecik.michal.tonictask.infrastructure.security.JwtService
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.users.api.UserDataDto
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

class ServerResponseCreator(private val objectMapper: ObjectMapper, private val jwtService: JwtService) {

    fun <T : Any> okFromMono(mono: () -> Mono<T>): Mono<ServerResponse> {
        return mono().flatMap { result ->
            ok().bodyValue(objectMapper.convertValue(result))
        }
    }

    fun <T : Any> fromMonoEither(monoEither: () -> MonoEither<T>): Mono<ServerResponse> {
        return monoEither().flatMap {
            it.map { result ->
                ok().bodyValue(objectMapper.convertValue(result))
            }.getOrElseGet { error ->
                status(resolveStatus(error))
                        .bodyValue(error)
            }
        }
    }

    fun fromUserData(monoEither: () -> MonoEither<UserDataDto>): Mono<ServerResponse> {
        return monoEither().flatMap {
            it.map { result ->
                ok()
                        .header(AUTH_HEADER_KEY, "$BEARER ${jwtService.generateJwt(result)}")
                        .bodyValue(objectMapper.convertValue(result))
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

}



