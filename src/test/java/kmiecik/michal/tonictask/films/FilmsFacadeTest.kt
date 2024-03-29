package kmiecik.michal.tonictask.films

import kmiecik.michal.tonictask.TestSamples.sampleCatalog
import kmiecik.michal.tonictask.TestUtils.valueOfMono
import kmiecik.michal.tonictask.infrastructure.omdb.OmdbService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FilmsFacadeTest {

    private lateinit var facade: FilmsFacade

    @BeforeEach
    fun setup() {
        facade = FilmsModule().createInMemoryFacade()
    }

    @Test
    fun shouldCreateFilmCatalog() {
        // given empty catalog

        // when create catalog
        val result = facade.createFilmCatalog(sampleCatalog())

        // then catalog is created
        val valueOfMono = valueOfMono(result)
        val savedFilms = valueOfMono(facade.listFilms())

        assertEquals(3, valueOfMono.size())
        assertEquals(3, savedFilms.size())
    }

    @Test
    fun shouldNotCreateFilmCatalogWhenExists() {
        // given catalog
        facade.createFilmCatalog(sampleCatalog()).block()

        // when create catalog
        val result = facade.createFilmCatalog(sampleCatalog())

        // then catalog is created
        val savedFilms = valueOfMono(result)
        val allFilms = valueOfMono(facade.listFilms())

        assertEquals(3, savedFilms.size())
        assertEquals(3, allFilms.size())
    }
}
