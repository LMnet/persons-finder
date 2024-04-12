# Welcome

This is a start-up Git repo for a project we are calling 'Persons finder'.

Your job is to create a set of API's that will feed a mobile application that has one purpose: find people around you.

Requirements:
- POST API to create a 'person'
- PUT API to update/create someone's location using latitude and longitude
- GET API to retrieve people around query location with a radius in KM, Use query param for radius. Extra challenge: Return list ordered by distance to each person.
- GET API to retrieve a person or persons name using their ids
- Responses must follow a JSON format

You'll also need to build the logic and services for saving/retrieving locations and persons.

Steps:
- Clone the project
- Implement required API's and services
- Push your project to your personal github


### Extra challenge
### Add a million, 10 million and 100 million entries and challenge your API's efficiency 

For any questions, please reach out on: leo@getsquareone.app


# Implementation notes

* Instead of H2, I used PostgreSQL with the excellent PostGIS extension. I believe that using the same database as in production could save from a lot of surprises. For this kind of app, PostgreSQL + PostGIS is the de facto standard solution. Also, I used docker-compose to simplify local testing.
* For DB migrations, I used Flyway.
* I changed some APIs, interfaces, and some naming.
* Error handling is somewhat half-baked. Obvious cases are handled, but errors are not consistent in terms of format.
* I haven't implemented tests. I have manually tested happy-path scenarios, as well as invalid cases. For a real production app, tests are required. For this kind of app, I would write mostly unit tests, plus a few testcontainers tests for the database.


## How to start the app

Before starting an application containers from the `docker-compose.yml` should be started:
```
docker-compose up -d
```
After that you can run an app from gradle:
```
./gradlew bootRun
```
