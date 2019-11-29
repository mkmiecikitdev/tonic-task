package kmiecik.michal.tonictask.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import kmiecik.michal.tonictask.infrastructure.security.JwtService
import kmiecik.michal.tonictask.repertoire.RepertoireFacade
import kmiecik.michal.tonictask.repertoire.api.AddRepertoireTimeDto
import kmiecik.michal.tonictask.repertoire.api.RemoveRepertoireTimeDto
import kmiecik.michal.tonictask.repertoire.api.UpdateFilmPriceDto
import kmiecik.michal.tonictask.repertoire.api.UpdateRepertoireTimeDto
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono


class RepertoireHandler(private val repertoireFacade: RepertoireFacade, private val jwtService: JwtService, private val objectMapper: ObjectMapper) {

    fun routes() = router {
        "/repertoires".nest {
            POST("/updateprice", this@RepertoireHandler::updateFilmPrice)
            POST("/addtime", this@RepertoireHandler::addFilmTime)
            POST("/removetime", this@RepertoireHandler::removeFilmTime)
            POST("/updatetime", this@RepertoireHandler::updateFilmTime)
        }
    }

    private fun updateFilmPrice(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<UpdateFilmPriceDto>().flatMap {
                repertoireFacade.updateFilmPrice(it).resolveEither(objectMapper)
            }
        }
    }

    private fun addFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<AddRepertoireTimeDto>().flatMap {
                repertoireFacade.addFilmTime(it).resolveEither(objectMapper)
            }
        }
    }

    private fun removeFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<RemoveRepertoireTimeDto>().flatMap {
                repertoireFacade.removeFilmTime(it).resolveEither(objectMapper)
            }
        }
    }

    private fun updateFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<UpdateRepertoireTimeDto>().flatMap {
                repertoireFacade.updateFilmTime(it).resolveEither(objectMapper)
            }
        }
    }
}
