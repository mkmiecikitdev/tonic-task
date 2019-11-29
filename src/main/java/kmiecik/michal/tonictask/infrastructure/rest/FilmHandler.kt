package kmiecik.michal.tonictask.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.NewCatalogDto
import kmiecik.michal.tonictask.infrastructure.security.JwtService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class FilmHandler(private val filmsFacade: FilmsFacade, private val jwtService: JwtService, private val objectMapper: ObjectMapper) {

    fun routes() = router {
        "/films".nest {
            POST("/catalog", this@FilmHandler::createFilmCatalog)
            GET("/list", this@FilmHandler::listFilm)
        }
    }

    private fun createFilmCatalog(req: ServerRequest): Mono<ServerResponse> {
        return onlyOwners(req, { jwtService.getUserData(it) }) { request, userData ->
            request.bodyToMono<NewCatalogDto>().flatMap {
                filmsFacade.createFilmCatalog(it)
                        .flatMap { result -> ServerResponse.ok().bodyValue(result) }
            }
        }
    }

    private fun listFilm(req: ServerRequest): Mono<ServerResponse> {
        return filmsFacade.listFilms()
                .flatMap { ServerResponse.ok().bodyValue(objectMapper.convertValue(it)) }
    }

}
