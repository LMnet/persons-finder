package com.persons.finder.domain.services

import com.persons.finder.data.Location
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.SQLException
import javax.transaction.Transactional

@Service
class LocationsServiceImpl(
    private val jdbcTemplate: JdbcTemplate
) : LocationsService {

    @Transactional
    override fun addLocation(location: Location) {
        val sql = """
            insert into persons_finder.locations (reference_id, location) 
            values (?, st_makepoint(?, ?))
            on conflict (reference_id) 
            do update set location = excluded.location
        """
        jdbcTemplate.update(sql, location.referenceId, location.longitude, location.latitude)
    }

    override fun findAround(referenceId: Long, radiusInKm: Double): List<LocationsService.LocationWithDistance> {
        val getReferenceLocationSql = """
            select 
              reference_id,
              st_x(location) as longitude, 
              st_y(location) as latitude 
            from persons_finder.locations 
            where reference_id = ?
            """
        val referenceLocation: Location? =
            jdbcTemplate.query(getReferenceLocationSql, locationRowMapper, referenceId).firstOrNull()

        if (referenceLocation == null) return emptyList()

        val sql = """
            select
              reference_id,
              st_x(location) as longitude,
              st_y(location) as latitude,
              st_distancesphere(
                st_makepoint(${referenceLocation.longitude}, ${referenceLocation.latitude}), 
                location
              ) as distance
            from persons_finder.locations
            where
              st_dwithin(
                st_makepoint(${referenceLocation.longitude}, ${referenceLocation.latitude})::geography, 
                location::geography, 
                ?
              ) and reference_id != ?
            order by distance
        """

        return jdbcTemplate.query(
            sql,
            { rs, rowNum ->
                val location = locationRowMapper.mapRow(rs, rowNum) ?: throw SQLException("Failed to get location")
                val distanceInKm = rs.getDouble("distance") / 1000
                LocationsService.LocationWithDistance(location, distanceInKm)
            },
            radiusInKm * 1000,
            referenceId
        )
    }

    private val locationRowMapper: RowMapper<Location> = RowMapper { rs, _ ->
        Location(
            referenceId = rs.getLong("reference_id"),
            latitude = rs.getDouble("latitude"),
            longitude = rs.getDouble("longitude"),
        )
    }
}
