package kmiecik.michal.tonictask.infrastructure.csv

import io.vavr.collection.List
import io.vavr.control.Try
import kmiecik.michal.tonictask.films.FilmExternalId
import kmiecik.michal.tonictask.films.FilmExternalIdType
import kmiecik.michal.tonictask.films.api.NewCatalogDto
import kmiecik.michal.tonictask.films.api.NewFilmDto
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class CsvReader {

    fun read(): NewCatalogDto {
        var films: List<NewFilmDto> = List.empty()

        return javaClass.classLoader.getResource(FILM_CATALOG_FILE)?.toURI()?.let {
            Try.of {
                BufferedReader(FileReader(File(it))).let {
                    var line = it.readLine()
                    while (line != null) {
                        val words = line.split(CSV_SPLIT_BY)
                        films = films.append(NewFilmDto(name = words[0], externalIds = List.of(FilmExternalId(words[1], FilmExternalIdType.OMDb))))
                        line = it.readLine()
                    }
                    return@of NewCatalogDto(films)
                }
            }.getOrElseGet { emptyCatalog() }
        } ?: emptyCatalog()
    }

    private fun emptyCatalog(): NewCatalogDto {
        return NewCatalogDto(List.empty())
    }

    companion object {
        private const val FILM_CATALOG_FILE = "catalog.csv"
        private const val CSV_SPLIT_BY = ";"
    }

}