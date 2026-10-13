# Sleep Tracker

Sleep Tracker brings manual sleep logs and Garmin records into one dashboard. Users can follow changes in sleep duration, quality, and schedule, and explore how those patterns relate to daily habits.

## The app

A React/TypeScript dashboard backed by Spring Boot and PostgreSQL. It shows sleep duration, quality, schedule trends, and habit associations. These comparisons do not establish cause and effect.

## Run

With Docker installed:

```sh
docker compose up --build
```

Open [localhost:8080](http://localhost:8080). The included database credentials are for local development. API documentation is at [/api/docs](http://localhost:8080/api/docs).

## Development

- Backend: Java 25 and Docker; run `./mvnw verify` in `backend/`.
- Frontend: Node.js 24; run `npm install` and `npm test` in `frontend/`.

There are no user accounts. Sleep estimates are not medical advice.

Started as a CPSC 233 JavaFX group project with Dylan Crombie and Gary Liao. The original version is preserved in [javafx-v1](https://github.com/manavpreet1809/Sleep-Tracker/tree/javafx-v1).
