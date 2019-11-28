package kmiecik.michal.tonictask

import kmiecik.michal.tonictask.films.FilmsModule
import kmiecik.michal.tonictask.infrastructure.rest.RepertoireHandler
import kmiecik.michal.tonictask.infrastructure.rest.UsersHandler
import kmiecik.michal.tonictask.ratings.RatingsModule
import kmiecik.michal.tonictask.repertoire.RepertoireModule
import kmiecik.michal.tonictask.users.UsersModule
import org.reactivestreams.Publisher
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import java.util.function.BiFunction

class Server {

    fun start(app: App) {

        val httpHandler = RouterFunctions
                .toHttpHandler(
                        UsersHandler(app.usersFacade).routes()
                                .and(RepertoireHandler(app.repertoireFacade).routes())

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

