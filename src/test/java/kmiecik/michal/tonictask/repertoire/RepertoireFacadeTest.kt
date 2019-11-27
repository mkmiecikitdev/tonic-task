package kmiecik.michal.tonictask.repertoire


import kmiecik.michal.tonictask.TestSamples.sampleFilms
import kmiecik.michal.tonictask.TestUtils.assertMonoEitherLeft
import kmiecik.michal.tonictask.TestUtils.assertMonoEitherRight
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.FilmsModule
import kmiecik.michal.tonictask.repertoire.api.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RepertoireFacadeTest {

    private lateinit var facade: RepertoireFacade
    private lateinit var filmsFacade: FilmsFacade

    @BeforeEach
    fun setup() {
        filmsFacade = FilmsModule().createInMemoryFacade()
        facade = RepertoireModule().createInMemoryFacade(filmsFacade)
    }

    @Test
    fun shouldReturnRepertoiresWithDefaultPriceAndNoDisplayTimes() {
        // given
        filmsFacade.createFilmCatalog(sampleFilms()).block()

        // when
        val result = facade.listExtendedDataRepertoires().block()

        // then

        assertNotNull(result)
        assertEquals(3, result.size())

        result.map {
            assertPriceIsDefault(it)
            assertEquals(0, it.times.size())
        }
    }

    @Test
    fun shouldUpdatePrice() {
        // given
        val form = UpdateFilmPriceDto(filmId = "1", price = "50", currency = "EUR")

        // when
        val result = facade.updateFilmPrice(form)
        // price

        assertMonoEitherRight(result) {
            assertEquals("EUR", it.priceCurrency)
            assertEquals("50", it.priceValue)
        }
    }

    @Test
    fun shouldReturnPriceParseError() {
        // given
        val form = UpdateFilmPriceDto(filmId = "1", price = "xxx", currency = "EUR")

        // when
        val result = facade.updateFilmPrice(form)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.CANNOT_PARSE_PRICE, it)
        }
    }

    @Test
    fun shouldReturnCurrencyParseError() {
        // given
        val form = UpdateFilmPriceDto(filmId = "1", price = "150", currency = "XXX")

        // when
        val result = facade.updateFilmPrice(form)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.CANNOT_PARSE_CURRENCY, it)
        }
    }

    @Test
    fun shouldAddTime() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")

        // when
        val result = facade.addFilmTime(addTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(1, it.times.size())
        }
    }

    @Test
    fun shouldNotDoubleTime() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.addFilmTime(addTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(1, it.times.size())
        }
    }

    @Test
    fun shouldReturnParseTimeError() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "aa:aa")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.addFilmTime(addTimeForm)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.CANNOT_PARSE_DATE, it)
        }
    }

    @Test
    fun shouldRemoveTimeIfExist() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")
        val removeTimeForm = RemoveRepertoireTimeDto(filmId = "1", time = "10:00")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.removeFilmTime(removeTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(0, it.times.size())
        }
    }

    @Test
    fun shouldNotRemoveTimeIfNotExist() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")
        val removeTimeForm = RemoveRepertoireTimeDto(filmId = "1", time = "15:00")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.removeFilmTime(removeTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(1, it.times.size())
            assertEquals("10:00", it.times[0])
        }
    }

    @Test
    fun shouldUpdateTimeIfExist() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")
        val updateTimeForm = UpdateRepertoireTimeDto(filmId = "1", timeFrom = "10:00", timeTo = "08:00")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.updateFilmTime(updateTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(1, it.times.size())
            assertEquals("08:00", it.times[0])
        }
    }

    @Test
    fun shouldNotUpdateTimeIfNoExist() {
        // given
        val addTimeForm = AddRepertoireTimeDto(filmId = "1", time = "10:00")
        val updateTimeForm = UpdateRepertoireTimeDto(filmId = "1", timeFrom = "12:00", timeTo = "15:00")

        // when
        facade.addFilmTime(addTimeForm).block()
        val result = facade.updateFilmTime(updateTimeForm)

        // then
        assertMonoEitherRight(result) {
            assertEquals(1, it.times.size())
            assertEquals("10:00", it.times[0])
        }
    }


    private fun assertPriceIsDefault(repertorie: ExtendedRepertoireDataDto) {
        assertEquals("EUR", repertorie.priceCurrency)
        assertEquals("10", repertorie.priceValue)
    }


}
