package kmiecik.michal.tonictask.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import kmiecik.michal.tonictask.infrastructure.rest.helpers.Constants.BEARER
import kmiecik.michal.tonictask.users.api.UserDataDto
import java.util.*


class JwtService(private val objectMapper: ObjectMapper) {

    fun generateJwt(userData: UserDataDto): String =
            Jwts.builder()
                    .signWith(Keys.hmacShaKeyFor(JWT_SECRET.toByteArray()), SignatureAlgorithm.HS512)
                    .setExpiration(Date(System.currentTimeMillis() + EXPIRATION))
                    .setSubject(objectMapper.writeValueAsString(userData))
                    .compact()

    fun getUserData(token: String): UserDataDto =
            Jwts.parser()
                    .setSigningKey(JWT_SECRET.toByteArray())
                    .parseClaimsJws(token.replace(BEARER, ""))
                    .body
                    .subject.let {
                objectMapper.readValue(it, UserDataDto::class.java)
            }

    companion object {
        private const val JWT_SECRET: String = "SgVkYp2s5v8y/B?E(H+MbQeThWmZq4t6w9z\$C&F)J@NcRfUjXn2r5u8x!A%D*G-KaPdSgVkYp3s6v9y\$B?E(H+MbQeThWmZq4t7w!z%C*F)J@NcRfUjXn2r5u8x/A?D(" // TODO FROM SAFE FILE
        private const val EXPIRATION = 864000000
    }
}
