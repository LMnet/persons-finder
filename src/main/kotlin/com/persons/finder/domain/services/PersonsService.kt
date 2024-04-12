package com.persons.finder.domain.services

import com.persons.finder.data.Person

interface PersonsService {
    fun getById(id: Long): Person?
    fun getByIds(ids: Set<Long>): Set<Person>
    fun create(person: Person.WithoutId): Person
}
