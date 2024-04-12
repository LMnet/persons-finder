package com.persons.finder.presentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@RestController
@RequestMapping("api/v1/persons")
@Validated
class PersonController @Autowired constructor(
    private val locationsService: LocationsService,
    private val personsService: PersonsService,
) {

    /*
        POST API to create a 'person'
        (JSON) Body and return the id of the created entity
    */
    @PostMapping("")
    fun createPerson(@RequestBody @Valid request: Person.WithoutId): ResponseEntity<CreatePersonResponse> {
        val newPerson = personsService.create(request)
        return ResponseEntity.ok(CreatePersonResponse(newPerson.id))
    }

    /*
        PUT API to update/create someone's location using latitude and longitude
     */
    @PutMapping("/{personId}/location")
    fun updateLocation(
        @PathVariable personId: Long,
        @RequestBody @Valid request: UpdateLocationRequest
    ): ResponseEntity<Any> {
        personsService.getById(personId) ?: return notFoundResponse(personId)

        val location = Location(personId, request.latitude, request.longitude)
        locationsService.addLocation(location)
        return ResponseEntity.ok(MessageBody("Location updated successfully"))
    }

    /*
        GET API to retrieve people around query location with a radius in KM, Use query param for radius.
        API just return a list of persons ids (JSON)
        // Example
        // John wants to know who is around his location within a radius of 10km
        // API would be called using John's id and a radius 10km
     */
    @GetMapping("/{personId}/nearby")
    fun findPeopleNearby(
        @PathVariable personId: Long,
        @RequestParam("radius") @Min(0) radiusInKm: Double
    ): ResponseEntity<Any> {
        val person = personsService.getById(personId) ?: return notFoundResponse(personId)

        val locations: List<LocationsService.LocationWithDistance> = locationsService.findAround(person.id, radiusInKm)
        return ResponseEntity.ok(locations)
    }

    /*
        GET API to retrieve a person or persons name using their ids
        // Example
        // John has the list of people around them, now they need to retrieve everybody's names to display in the app
        // API would be called using person or persons ids
     */
    @GetMapping("")
    fun getPersonsByIds(
        @RequestParam("ids") @NotEmpty @Size(min = 1, max = 10000) ids: List<Long>
    ): ResponseEntity<Set<Person>> {
        val persons = personsService.getByIds(ids.toSet())
        return ResponseEntity.ok(persons)
    }

    private fun notFoundResponse(personId: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorBody("""Person $personId not found"""))
    }

    data class CreatePersonResponse(
        val id: Long
    )

    data class UpdateLocationRequest(
        @field:Min(-90) @field:Max(90) @field:JsonProperty(required = true)
        val latitude: Double,
        @field:Min(-180) @field:Max(180) @field:JsonProperty(required = true)
        val longitude: Double
    )
}
