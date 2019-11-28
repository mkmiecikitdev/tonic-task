package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.infrastructure.security.JwtService
import kmiecik.michal.tonictask.users.UsersFacade
import kmiecik.michal.tonictask.users.api.UserFormDto
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class UsersHandler(private val usersFacade: UsersFacade, private val jwtService: JwtService) {

    fun routes() = router {
        POST("/addcustomer", this@UsersHandler::addCustomer)
        POST("/addowner", this@UsersHandler::addOwner)
        POST("/login", this@UsersHandler::login)
    }

    private fun addCustomer(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addCustomer(it).resolveEitherWithAuth { userData -> jwtService.generateJwt(userData) }
        }
    }

    private fun addOwner(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addOwner(it).resolveEitherWithAuth { userData -> jwtService.generateJwt(userData) }
        }
    }

    private fun login(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addOwner(it).resolveEitherWithAuth { userData -> jwtService.generateJwt(userData) }
        }
    }

}
