package kmiecik.michal.tonictask.repertoire

import io.vavr.Tuple
import io.vavr.collection.List
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.FilmDto
import kmiecik.michal.tonictask.repertoire.api.*
import reactor.core.publisher.Mono

class RepertoireFacade(private val repertoireRepository: RepertoireRepository,
                       private val filmsFacade: FilmsFacade,
                       private val timeUpdater: TimeUpdater,
                       private val priceUpdater: PriceUpdater) {

    fun updateFilmPrice(updateFilmPriceDto: UpdateFilmPriceDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(updateFilmPriceDto.filmId).flatMap { repertoire ->
            priceUpdater.updatePrice(repertoire, updateFilmPriceDto.price, updateFilmPriceDto.currency)
                    .map { save(it) }
                    .mapLeft { it.toMono<RepertoireDataDto>() }
                    .getOrElseGet { it }
        }
    }

    fun addFilmTime(addRepertoireTimeDto: AddRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(addRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.addTime(repertoire, addRepertoireTimeDto.time)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it }
                }
    }

    fun removeFilmTime(removeRepertoireTimeDto: RemoveRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(removeRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.removeTime(repertoire, removeRepertoireTimeDto.time)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it }
                }
    }

    fun updateFilmTime(updateRepertoireTimeDto: UpdateRepertoireTimeDto): Mono<Either<AppError, RepertoireDataDto>> {
        return getRepertoireOrEmpty(updateRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.updateTime(repertoire, updateRepertoireTimeDto.timeFrom, updateRepertoireTimeDto.timeTo)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .getOrElseGet { it }
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
