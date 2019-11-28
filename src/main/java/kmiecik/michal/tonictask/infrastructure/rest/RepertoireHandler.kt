package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.repertoire.RepertoireFacade
import kmiecik.michal.tonictask.repertoire.api.UpdateFilmPriceDto
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono


class RepertoireHandler(private val repertoireFacade: RepertoireFacade) {

    fun routes() = router {
        POST("/updatefilmprice", this@RepertoireHandler::updateFilmPrice)
    }

    private fun updateFilmPrice(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req) { request ->
            request.bodyToMono<UpdateFilmPriceDto>().flatMap {
                repertoireFacade.updateFilmPrice(it).resolveEither()
            }
        }
    }

}
