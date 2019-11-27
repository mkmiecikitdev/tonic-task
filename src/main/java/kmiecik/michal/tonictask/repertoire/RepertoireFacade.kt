package kmiecik.michal.tonictask.repertoire

import io.vavr.Tuple
import io.vavr.collection.List
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.errors.merge
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.FilmDto
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.kernel.MonoList
import kmiecik.michal.tonictask.repertoire.api.*
import reactor.core.publisher.Mono

class RepertoireFacade(private val repertoireRepository: RepertoireRepository,
                       private val filmsFacade: FilmsFacade,
                       private val timeUpdater: TimeUpdater,
                       private val priceUpdater: PriceUpdater) {

    fun updateFilmPrice(updateFilmPriceDto: UpdateFilmPriceDto): MonoEither<RepertoireDataDto> {
        return getRepertoireOrNew(updateFilmPriceDto.filmId).flatMap { repertoire ->
            priceUpdater.updatePrice(repertoire, updateFilmPriceDto.price, updateFilmPriceDto.currency)
                    .map { save(it) }
                    .mapLeft { it.toMono<RepertoireDataDto>() }
                    .merge()
        }
    }

    fun addFilmTime(addRepertoireTimeDto: AddRepertoireTimeDto): MonoEither<RepertoireDataDto> {
        return getRepertoireOrNew(addRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.addTime(repertoire, addRepertoireTimeDto.time)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .merge()
                }
    }

    fun removeFilmTime(removeRepertoireTimeDto: RemoveRepertoireTimeDto): MonoEither<RepertoireDataDto> {
        return getRepertoireOrNew(removeRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.removeTime(repertoire, removeRepertoireTimeDto.time)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .merge()
                }
    }

    fun updateFilmTime(updateRepertoireTimeDto: UpdateRepertoireTimeDto): MonoEither<RepertoireDataDto> {
        return getRepertoireOrNew(updateRepertoireTimeDto.filmId)
                .flatMap { repertoire ->
                    timeUpdater.updateTime(repertoire, updateRepertoireTimeDto.timeFrom, updateRepertoireTimeDto.timeTo)
                            .map { save(it) }
                            .mapLeft { it.toMono<RepertoireDataDto>() }
                            .merge()
                }
    }

    fun listExtendedRepertoiresData(): MonoList<ExtendedRepertoireDataDto> {
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

    private fun listRepertoires(): MonoList<RepertoireDataDto> {
        return repertoireRepository.findAll()
                .map { it.toDto() }
                .collectList()
                .map { List.ofAll(it) }
    }

    private fun save(repertoire: Repertoire): MonoEither<RepertoireDataDto> {
        return repertoireRepository.save(repertoire)
                .map { saved ->
                    Either.right<AppError, RepertoireDataDto>(saved.toDto())
                }
    }

    private fun getRepertoireOrNew(filmId: String): Mono<Repertoire> {
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
