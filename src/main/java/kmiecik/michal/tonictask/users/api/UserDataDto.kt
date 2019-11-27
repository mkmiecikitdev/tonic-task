package kmiecik.michal.tonictask.users.api

import io.vavr.collection.Set
import kmiecik.michal.tonictask.users.Role

data class UserDataDto(
        val login: String,
        val roles: Set<Role>
)