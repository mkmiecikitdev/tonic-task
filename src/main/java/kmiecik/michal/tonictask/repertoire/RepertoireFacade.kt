package kmiecik.michal.tonictask.repertoire

import io.vavr.control.Either
import io.vavr.control.Try
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.repertoire.api.RepertoireDto
import kmiecik.michal.tonictask.repertoire.api.UpdateFilmPriceDto
import reactor.core.publisher.Mono
import java.math.BigDecimal

class RepertoireFacade(private val repertoireRepository: RepertoireRepository) {

    fun updateFilmPrice(updateFilmPriceDto: UpdateFilmPriceDto): Mono<Either<AppError, RepertoireDto>> {

        val repertoire: Mono<Repertoire> = repertoireRepository.findByFilmId(updateFilmPriceDto.filmId)
                .defaultIfEmpty(newRepertoire(updateFilmPriceDto.filmId))

        val v: Mono<Either<AppError, Repertoire>> = repertoire.map {
            update(it, updateFilmPriceDto)
        }

        return v.map {
            it.map {

            }
        }
    }

    private fun update(repertoire: Repertoire, updateFilmPriceDto: UpdateFilmPriceDto): Either<AppError, Repertoire> {
        val currency: Either<AppError, Currency> = convertCurrency(updateFilmPriceDto.currency)
        val price: Either<AppError, BigDecimal> = convertPrice(updateFilmPriceDto.price)

        return currency.flatMap { c ->
            price.map { p ->
                repertoire.updatePrice(p, c)
            }
        }
    }

    private fun save(repertoire: Repertoire): Either<AppError, Mono<RepertoireDto>> {
        return repertoireRepository.save(repertoire)
                .map { it.toDto() }
                .map { Either.right(it) }
    }

    private fun newRepertoire(filmId: String): Repertoire {
        return Repertoire(
                filmId = filmId,
                displayHours = DisplayHours(),
                price = Price.default()
        )
    }

    private fun convertCurrency(currency: String): Either<AppError, Currency> {
        return Try.of { Currency.valueOf(currency) }
                .toEither(AppError.CANNOT_PARSE_CURRENCY)
    }

    private fun convertPrice(price: String): Either<AppError, BigDecimal> {
        return Try.of { BigDecimal(price) }
                .toEither(AppError.CANNOT_PARSE_PRICE)
    }

}