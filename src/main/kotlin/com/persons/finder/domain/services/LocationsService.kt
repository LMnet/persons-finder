package com.persons.finder.domain.services

import com.persons.finder.data.Location

interface LocationsService {
    fun addLocation(location: Location)
    fun findAround(referenceId: Long, radiusInKm: Double) : List<LocationWithDistance>

    data class LocationWithDistance(val location: Location, val distanceInKm: Double)
}
