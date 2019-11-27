package kmiecik.michal.tonictask.repertoire

import io.vavr.Tuple
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Try
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.FilmDto
import kmiecik.michal.tonictask.kernel.TimeUtils
import kmiecik.michal.tonictask.repertoire.api.*
import reactor.core.publisher.Mono
import java.math.BigDecimal

class RepertoireFacade(private val repertoireRepository: RepertoireRepository, private val filmsFacade: FilmsFacade) {

    fun updateFilmPrice(updateFilmPriceDto: UpdateFilmPriceDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(updateFilmPriceDto.filmId).flatMap { repertoire ->
            parseCurrency(updateFilmPriceDto.currency).flatMap { currency ->
                parsePrice(updateFilmPriceDto.price).map { price ->
                    repertoire.updatePrice(price, currency)
                    save(repertoire)
                }
            }.mapLeft { it.toMono<RepertoireDataDto>() }
                    .getOrElseGet { it }
        }
    }

    fun addFilmTime(addRepertoireTimeDto: AddRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(addRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    TimeUtils.toLocalTime(addRepertoireTimeDto.time)
                            .map { time ->
                                repertoire.addTime(time)
                                save(repertoire)
                            }.mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it }
                }
    }

    fun removeFilmTime(removeRepertoireTimeDto: RemoveRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(removeRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    TimeUtils.toLocalTime(removeRepertoireTimeDto.time)
                            .map { time ->
                                repertoire.removeTime(time)
                                save(repertoire)
                            }.mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it }
                }
    }

    fun updateFilmTime(updateRepertoireTimeDto: UpdateRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(updateRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    TimeUtils.toLocalTime(updateRepertoireTimeDto.timeFrom).flatMap { timeFrom ->
                        TimeUtils.toLocalTime(updateRepertoireTimeDto.timeTo)
                                .map { timeTo ->
                                    repertoire.updateTime(timeFrom, timeTo)
                                    save(repertoire)
                                }
                    }.mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it } // TODO W RAZIE JAK MERGE NIE DZIALA
                }
    }

    fun listExtendedDataRepertoires(): Mono<List<ExtendedRepertoireDataDto>> {
        val repertoireMap = listRepertoires().map {
            it.toMap { repertoire ->
                Tuple.of(repertoire.filmId, repertoire)
            }
        }

        return filmsFacade.listFilms()
                .flatMap { films ->
                    repertoireMap.map { repertoire ->
                        films.map { film ->
                            mergeRepertoireAndFilm(film, repertoire[film.id].getOrElse { newRepertoire(film.id).toDto() })
                        }
                    }
                }
    }

    private fun mergeRepertoireAndFilm(filmDto: FilmDto, repertoireDataDto: RepertoireDataDto): ExtendedRepertoireDataDto {
        return ExtendedRepertoireDataDto(
                filmId = filmDto.id,
                filmName = filmDto.name,
                priceCurrency = repertoireDataDto.priceCurrency,
                priceValue = repertoireDataDto.priceValue,
                times = repertoireDataDto.times
        )
    }

    private fun listRepertoires(): Mono<List<RepertoireDataDto>> {
        return repertoireRepository.findAll()
                .map { it.toDto() }
                .collectList()
                .map { List.ofAll(it) }
    }

    private fun save(repertoire: Repertoire): Mono<Either<AppError, RepertoireDataDto>> {
        return repertoireRepository.save(repertoire)
                .map { saved ->
                    Either.right<AppError, RepertoireDataDto>(saved.toDto())
                }
    }


    private fun parseCurrency(currency: String): Either<AppError, Currency> {
        return Try.of { Currency.valueOf(currency) }
                .toEither(AppError.CANNOT_PARSE_CURRENCY)
    }

    private fun parsePrice(price: String): Either<AppError, BigDecimal> {
        return Try.of { BigDecimal(price) }
                .toEither(AppError.CANNOT_PARSE_PRICE)
    }

    private fun getRepertoireOrEmpty(filmId: String): Mono<Repertoire> {
        return repertoireRepository.findByFilmId(filmId)
                .defaultIfEmpty(newRepertoire(filmId))
    }

    private fun newRepertoire(filmId: String): Repertoire {
        return Repertoire(
                filmId = filmId,
                displayHours = DisplayHours(),
                price = Price.default()
        )
    }

}
