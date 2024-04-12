package com.persons.finder.data

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class Person(
    val id: Long,
    val name: String
) {
    data class WithoutId(
        @field:NotBlank(message = "Name must not be empty")
        @field:Size(min = 1, max = 1000, message = "Name must be within 1 and 1000 symbols in length")
        val name: String
    )
}
