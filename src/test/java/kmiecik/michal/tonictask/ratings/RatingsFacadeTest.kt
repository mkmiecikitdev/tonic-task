package kmiecik.michal.tonictask.ratings

import kmiecik.michal.tonictask.TestSamples
import kmiecik.michal.tonictask.TestUtils
import kmiecik.michal.tonictask.TestUtils.assertMonoEitherLeft
import kmiecik.michal.tonictask.TestUtils.assertMonoEitherRight
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.FilmsModule
import kmiecik.michal.tonictask.ratings.api.AddRatingDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RatingsFacadeTest {

    private lateinit var facade: RatingsFacade
    private lateinit var filmsFacade: FilmsFacade

    @BeforeEach
    fun setup() {
        filmsFacade = FilmsModule().createInMemoryFacade()
        facade = RatingsModule().createInMemoryFacade(filmsFacade)
    }

    @Test
    fun shouldReturnRatingsWithNoAverage() {
        // given
        filmsFacade.createFilmCatalog(TestSamples.sampleFilms()).block()

        // when
        val result = facade.listExtendedRaintsData()

        // then

        TestUtils.assertMono(result) {
            assertEquals(3, it.size())

            it.map { item ->
                assertEquals(0.0, item.average)
                assertTrue { item.filmName.isNotBlank() }
            }
        }
    }

    @Test
    fun shouldAddNewRating() {
        // given
        val form = AddRatingDto(value = 2, filmId = "1")

        // when
        val result = facade.addRate("user1", form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("1", it.filmId)
            assertEquals(2.0, it.average)
        }
    }

    @Test
    fun shouldNotDoubleRatingForTheSameUser() {
        // given
        val form = AddRatingDto(value = 2, filmId = "1")
        facade.addRate("user1", form).block()

        // when
        val result = facade.addRate("user1", form)

        // then
        assertMonoEitherRight(result) {
            assertEquals(2.0, it.average)
        }
    }

    @Test
    fun shouldCalculateCorrectAverage() {
        // given
        val form = AddRatingDto(value = 2, filmId = "1")
        facade.addRate("user1", form).block()
        val form2 = AddRatingDto(value = 4, filmId = "1")

        // when
        val result = facade.addRate("user2", form2)

        // then
        assertMonoEitherRight(result) {
            assertEquals(3.0, it.average)
        }
    }

    @Test
    fun shouldReturnInvalidRatingErrors() {
        // given
        val invalidForm1 = AddRatingDto(value = 0, filmId = "1")
        val invalidForm2 = AddRatingDto(value = 6, filmId = "1")

        // when
        val result = facade.addRate("user1", invalidForm1)
        val result2 = facade.addRate("user1", invalidForm2)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.INVALID_RATING, it)
        }

        assertMonoEitherLeft(result2) {
            assertEquals(AppError.INVALID_RATING, it)
        }
    }

}