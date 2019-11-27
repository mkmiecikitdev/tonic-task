package kmiecik.michal.tonictask.users

import io.vavr.collection.Set
import kmiecik.michal.tonictask.users.api.UserDataDto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class User (
    val login: String,
    private val hashedPassword: String,
    private val roles: Set<Role>
) {

    fun equalsHashedPass(rawPassword: String): Boolean {
        return BCryptPasswordEncoder().matches(rawPassword, hashedPassword)
    }

    fun toDto(): UserDataDto {
        return UserDataDto(
                login = login,
                roles = roles
        )
    }
}