package kmiecik.michal.tonictask.infrastructure.omdb

import kmiecik.michal.tonictask.films.FilmsExternalService
import kmiecik.michal.tonictask.films.api.FilmDetailsDto
import kmiecik.michal.tonictask.infrastructure.files.FileUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class OmdbService(
        private val baseUrl: String = FileUtils.loadProperty("application.properties", "omdb.url"),
        private val apikey: String = FileUtils.loadProperty("keys.properties", "omdb.key"),
        private val webClient: WebClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build()
) : FilmsExternalService {

    override fun fetchDetails(externalId: String): Mono<FilmDetailsDto> {
        return webClient.get()
                .uri {
                    it
                            .queryParam("i", externalId)
                            .queryParam("apikey", apikey)
                            .build()
                }
                .retrieve()
                .bodyToMono(OMDBFilm::class.java)
                .map {
                    FilmDetailsDto(
                            name = it.Title,
                            actors = it.Actors,
                            director = it.Director,
                            runtime = it.Runtime,
                            year = it.Year
                    )
                }
    }
}