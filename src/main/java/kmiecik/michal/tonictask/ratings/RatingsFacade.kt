package kmiecik.michal.tonictask.ratings

import io.vavr.Tuple
import io.vavr.collection.List
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.errors.merge
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.FilmDto
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.kernel.MonoList
import kmiecik.michal.tonictask.ratings.api.AddRatingDto
import kmiecik.michal.tonictask.ratings.api.ExtendedRatingsDataDto
import kmiecik.michal.tonictask.ratings.api.RatingsDataDto
import reactor.core.publisher.Mono

class RatingsFacade(private val ratingsRepository: RatingsRepository, private val filmsFacade: FilmsFacade) {

    fun addRate(userId: String, addRatingDto: AddRatingDto): MonoEither<RatingsDataDto> {
        return getRatingsOrNew(addRatingDto.filmId).flatMap { ratings ->
            ratings.addRate(userId, addRatingDto.value).map { updatedRatings ->
                save(updatedRatings)
            }
                    .mapLeft { e -> e.toMono<RatingsDataDto>() }
                    .merge()
        }
    }

    fun listExtendedRaintsData(): MonoList<ExtendedRatingsDataDto> {
        val ratingsMap = listRatings().map {
            it.toMap { ratings ->
                Tuple.of(ratings.filmId, ratings)
            }
        }

        return filmsFacade.listFilms()
                .flatMap { films ->
                    ratingsMap.map { ratings ->
                        films.map { film ->
                            mergeRatingsAndFilm(film, ratings[film.id].getOrElse { newRatings(film.id).toDto() })
                        }
                    }
                }
    }

    private fun listRatings(): MonoList<RatingsDataDto> {
        return ratingsRepository.findAll()
                .map { it.toDto() }
                .collectList()
                .map { List.ofAll(it) }
    }

    private fun save(ratings: Ratings): MonoEither<RatingsDataDto> {
        return ratingsRepository.save(ratings)
                .map { saved ->
                    Either.right<AppError, RatingsDataDto>(saved.toDto())
                }
    }


    private fun getRatingsOrNew(filmId: String): Mono<Ratings> {
        return ratingsRepository.findByFilmId(filmId)
                .defaultIfEmpty(newRatings(filmId))
    }

    private fun newRatings(filmId: String): Ratings {
        return Ratings(
                filmId = filmId,
                ratings = List.empty()
        )
    }

    private fun mergeRatingsAndFilm(filmDto: FilmDto, ratingsDataDto: RatingsDataDto): ExtendedRatingsDataDto {
        return ExtendedRatingsDataDto(
                filmId = filmDto.id,
                filmName = filmDto.name,
                average = ratingsDataDto.average
        )
    }

}