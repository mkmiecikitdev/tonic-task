package kmiecik.michal.tonictask.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.vavr.control.Try
import kmiecik.michal.tonictask.infrastructure.rest.helpers.Constants.BEARER
import kmiecik.michal.tonictask.users.api.UserDataDto
import java.util.*


class JwtService(private val objectMapper: ObjectMapper) {

    fun generateJwt(userData: UserDataDto): String {
        println(getSecret())
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(getSecret().toByteArray()), SignatureAlgorithm.HS512)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION))
                .setSubject(objectMapper.writeValueAsString(userData))
                .compact()
    }


    fun getUserData(token: String): UserDataDto {
        println(getSecret())
        return Jwts.parser()
                .setSigningKey(getSecret().toByteArray())
                .parseClaimsJws(token.replace(BEARER, ""))
                .body
                .subject.let {
            objectMapper.readValue(it, UserDataDto::class.java)
        }
    }


    private fun getSecret(): String {
        return Try.of {
            javaClass.classLoader.getResourceAsStream("sensitive.properties").let {
                val prop = Properties()
                prop.load(it)
                prop.getProperty("jwt.secret")
            }
        }.getOrElseGet { "" }
    }

    companion object {
        private const val EXPIRATION = 864000000
    }
}
