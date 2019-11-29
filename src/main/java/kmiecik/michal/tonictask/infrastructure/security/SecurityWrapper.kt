package kmiecik.michal.tonictask.infrastructure.security

import io.vavr.control.Try
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.infrastructure.rest.helpers.Constants.AUTH_HEADER_KEY
import kmiecik.michal.tonictask.users.Role
import kmiecik.michal.tonictask.users.api.UserDataDto
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class SecurityWrapper(private val jwtService: JwtService) {

    fun onlyOwners(req: ServerRequest, action: (ServerRequest, UserDataDto) -> Mono<ServerResponse>): Mono<ServerResponse> {
        return Try.of {
            jwtService.getUserData(req.headers().header(AUTH_HEADER_KEY)[0])
                    .let {
                        return@let if (it.roles.contains(Role.OWNER))
                            action(req, it)
                        else
                            unauthorized()
                    }

        }.getOrElseGet { unauthorized() }
    }

    private fun unauthorized(): Mono<ServerResponse> {
        return ServerResponse.status(resolveStatus(AppError.UNAUTHORIZED)).bodyValue(AppError.UNAUTHORIZED)
    }

    private fun resolveStatus(appError: AppError): HttpStatus {
        return when (appError) {
            AppError.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            AppError.BAD_CREDENTIALS -> HttpStatus.NOT_ACCEPTABLE
            else -> HttpStatus.BAD_REQUEST
        }
    }
}
