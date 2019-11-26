package kmiecik.michal.tonictask.films

import io.vavr.collection.List
import kmiecik.michal.tonictask.TestUtils.valueOfMono
import kmiecik.michal.tonictask.films.api.NewFilm
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
        val result = facade.createFilmCatalog(sampleFilms())

        // then catalog is created
        val valueOfMono = valueOfMono(result)
        val savedFilms = valueOfMono(facade.getFilmsData())

        assertEquals(3, valueOfMono.size())
        assertEquals(3, savedFilms.size())
    }

    @Test
    fun shouldNotCreateFilmCatalogWhenExists() {
        // given catalog
        facade.createFilmCatalog(sampleFilms()).block()

        // when create catalog
        val result = facade.createFilmCatalog(sampleFilms())

        // then catalog is created
        val savedFilms = valueOfMono(result)
        val allFilms = valueOfMono(facade.getFilmsData())

        assertEquals(3, savedFilms.size())
        assertEquals(3, allFilms.size())
    }

    private fun sampleFilms(): List<NewFilm> {
        return List.of(
                NewFilm(name = "Film 1", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilm(name = "Film 2", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb))),
                NewFilm(name = "Film 3", externalIds = List.of(FilmExternalId(id = "external1", type = FilmExternalIdType.OMDb)))
        )
    }


}