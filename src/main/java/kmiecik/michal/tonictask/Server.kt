package kmiecik.michal.tonictask

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vavr.jackson.datatype.VavrModule
import kmiecik.michal.tonictask.infrastructure.rest.FilmHandler
import kmiecik.michal.tonictask.infrastructure.rest.RepertoireHandler
import kmiecik.michal.tonictask.infrastructure.rest.UsersHandler
import kmiecik.michal.tonictask.infrastructure.security.JwtService
import org.reactivestreams.Publisher
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import java.util.function.BiFunction

class Server {

    fun start(app: App) {
        val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(VavrModule())
        val jwtService = JwtService(objectMapper)

        val httpHandler = RouterFunctions
                .toHttpHandler(
                        UsersHandler(app.usersFacade, jwtService, objectMapper).routes()
                                .and(RepertoireHandler(app.repertoireFacade, jwtService, objectMapper).routes())
                                .and(FilmHandler(app.filmsFacade, jwtService, objectMapper).routes())

                )

        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        run(adapter)
    }

    private fun run(adapter: BiFunction<in HttpServerRequest, in HttpServerResponse, out Publisher<Void>>) {
        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(adapter)
                .bindNow()
                .onDispose()
                .block()
    }

}

