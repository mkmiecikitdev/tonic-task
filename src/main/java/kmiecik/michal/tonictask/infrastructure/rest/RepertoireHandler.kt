package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.infrastructure.security.JwtService
import kmiecik.michal.tonictask.repertoire.RepertoireFacade
import kmiecik.michal.tonictask.repertoire.api.UpdateFilmPriceDto
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono


class RepertoireHandler(private val repertoireFacade: RepertoireFacade, private val jwtService: JwtService) {

    fun routes() = router {
        POST("/updatefilmprice", this@RepertoireHandler::updateFilmPrice)
    }

    private fun updateFilmPrice(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<UpdateFilmPriceDto>().flatMap {
                repertoireFacade.updateFilmPrice(it).resolveEither()
            }
        }
    }

}
