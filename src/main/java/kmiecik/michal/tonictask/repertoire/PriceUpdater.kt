package kmiecik.michal.tonictask.repertoire

import io.vavr.control.Either
import io.vavr.control.Try
import kmiecik.michal.tonictask.errors.AppError
import java.math.BigDecimal

class PriceUpdater {

    fun updatePrice(repertoire: Repertoire, price: String, currency: String): Either<AppError, Repertoire> {
        return parseCurrency(currency).flatMap { parsedCurrency ->
            parsePrice(price).map { parsedPrice ->
                repertoire.updatePrice(parsedPrice, parsedCurrency)
            }
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
}
