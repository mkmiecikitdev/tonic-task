package kmiecik.michal.tonictask.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.vavr.control.Option
import io.vavr.control.Try
import kmiecik.michal.tonictask.infrastructure.rest.helpers.Constants.BEARER
import kmiecik.michal.tonictask.users.api.UserDataDto
import java.util.*


class JwtService(private val objectMapper: ObjectMapper, private val secret: ByteArray = getSecret()) {

    fun generateJwt(userData: UserDataDto): String {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS512)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION))
                .setSubject(objectMapper.writeValueAsString(userData))
                .compact()
    }


    fun getUserData(token: String): Option<UserDataDto> {
        return Try.of {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.replace(BEARER, ""))
                    .body
                    .subject.let {
                objectMapper.readValue(it, UserDataDto::class.java)
            }
        }.toOption()
    }

    companion object {
        private const val EXPIRATION = 864000000

        private fun getSecret(): ByteArray {
            return javaClass.classLoader.getResourceAsStream("keys.properties").let {
                val prop = Properties()
                prop.load(it)
                it.close()
                prop.getProperty("jwt.secret")
                        .toByteArray()
            }
        }

    }
}
