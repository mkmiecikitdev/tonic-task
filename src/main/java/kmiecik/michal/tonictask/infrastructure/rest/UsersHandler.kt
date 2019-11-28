package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.users.UsersFacade
import kmiecik.michal.tonictask.users.api.UserFormDto
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

class UsersHandler(private val usersFacade: UsersFacade) {

    fun routes() = router {
        POST("/addcustomer", this@UsersHandler::addCustomer)
        POST("/addowner", this@UsersHandler::addOwner)
        POST("/login", this@UsersHandler::login)
    }

    private fun addCustomer(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addCustomer(it).resolveEither()
        }
    }

    private fun addOwner(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addOwner(it).resolveEither()
        }
    }

    private fun login(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<UserFormDto>().flatMap {
            usersFacade.addOwner(it).resolveEither()
        }
    }

}