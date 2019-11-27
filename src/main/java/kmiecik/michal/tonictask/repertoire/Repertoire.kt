package kmiecik.michal.tonictask.repertoire

import kmiecik.michal.tonictask.kernel.parse
import kmiecik.michal.tonictask.repertoire.api.RepertoireDataDto
import java.math.BigDecimal
import java.time.LocalTime

data class Repertoire(
        val filmId: String,
        private var price: Price,
        private var displayHours: DisplayHours
) {

    fun updatePrice(value: BigDecimal, currency: Currency): Repertoire {
        price = Price(value = value, currency = currency)
        return this
    }

    fun addTime(time: LocalTime): Repertoire {
        displayHours = displayHours.addTime(time)
        return this
    }

    fun removeTime(time: LocalTime): Repertoire {
        displayHours = displayHours.removeTime(time)
        return this
    }

    fun updateTime(from: LocalTime, to: LocalTime): Repertoire {
        displayHours = displayHours.updateTime(from, to)
        return this
    }

    fun toDto(): RepertoireDataDto {
        return RepertoireDataDto(
                filmId = filmId,
                priceValue = price.value.toString(),
                priceCurrency = price.currency.name,
                times = displayHours.times.map { it.parse() }
        )
    }

}
