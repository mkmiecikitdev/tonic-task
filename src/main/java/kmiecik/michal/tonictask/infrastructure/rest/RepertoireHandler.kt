package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.infrastructure.rest.helpers.ServerResponseCreator
import kmiecik.michal.tonictask.infrastructure.security.SecurityWrapper
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


class RepertoireHandler(private val repertoireFacade: RepertoireFacade, private val securityWrapper: SecurityWrapper, private val serverResponseCreator: ServerResponseCreator) {

    fun routes() = router {
        "/repertoires".nest {
            POST("/updateprice", this@RepertoireHandler::updateFilmPrice)
            POST("/addtime", this@RepertoireHandler::addFilmTime)
            POST("/removetime", this@RepertoireHandler::removeFilmTime)
            POST("/updatetime", this@RepertoireHandler::updateFilmTime)
        }
    }

    private fun updateFilmPrice(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.onlyOwners(req) { request, _ ->
            request.bodyToMono<UpdateFilmPriceDto>().flatMap {
                serverResponseCreator.fromMonoEither { repertoireFacade.updateFilmPrice(it) }
                serverResponseCreator.fromMonoEither { repertoireFacade.updateFilmPrice(it) }
            }
        }
    }

    private fun addFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.onlyOwners(req) { request, _ ->
            request.bodyToMono<AddRepertoireTimeDto>().flatMap {
                serverResponseCreator.fromMonoEither { repertoireFacade.addFilmTime(it) }
            }
        }
    }

    private fun removeFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.onlyOwners(req) { request, _ ->
            request.bodyToMono<RemoveRepertoireTimeDto>().flatMap {
                serverResponseCreator.fromMonoEither { repertoireFacade.removeFilmTime(it) }
            }
        }
    }

    private fun updateFilmTime(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.onlyOwners(req) { request, _ ->
            request.bodyToMono<UpdateRepertoireTimeDto>().flatMap {
                serverResponseCreator.fromMonoEither { repertoireFacade.updateFilmTime(it) }
            }
        }
    }
}
