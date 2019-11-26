package kmiecik.michal.tonictask.repertoire

import java.math.BigDecimal

data class Price(val value: BigDecimal, val currency: Currency) {

    companion object {

        fun default() = Price(BigDecimal.TEN, Currency.EUR)

    }

}