package com.persons.finder.domain.services

import com.persons.finder.data.Person
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import java.sql.Statement
import javax.transaction.Transactional

@Service
@Transactional
class PersonsServiceImpl(
    private val jdbcTemplate: JdbcTemplate
) : PersonsService {

    override fun getById(id: Long): Person? {
        val sql = "select id, name from persons_finder.persons where id = ?"
        return jdbcTemplate.query(sql, personRowMapper, id).firstOrNull()
    }

    override fun getByIds(ids: Set<Long>): Set<Person> {
        val parameters = ids.joinToString(separator = ",", prefix = "(", postfix = ")")
        val sql = "select id, name from persons_finder.persons where id in $parameters"
        return jdbcTemplate.query(sql, personRowMapper).toSet()
    }

    override fun create(person: Person.WithoutId): Person {
        val sql = "insert into persons_finder.persons (name) values (?) returning id"
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({
            val ps = it.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, person.name)
            ps
        }, keyHolder)
        val newId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to create person")
        return Person(id = newId, name = person.name)
    }

    private val personRowMapper: RowMapper<Person> = RowMapper { rs, _ ->
        Person(
            id = rs.getLong("id"),
            name = rs.getString("name")
        )
    }

}
