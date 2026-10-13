# Sleep Tracker

Log your sleep, import Garmin data, and see which habits line up with better nights.
Java 25 + Spring Boot 4 backend, PostgreSQL, React + TypeScript frontend.

![Sleep Tracker dashboard](docs/images/dashboard.png)

## Run with Docker (app + PostgreSQL)

```sh
docker compose up --build
```

App: http://localhost:8080 · API docs: http://localhost:8080/api/docs

## Run for development

Backend with in-memory database (needs Java 25):

```sh
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Frontend with hot reload on http://localhost:5173 (needs Node 24):

```sh
cd frontend
npm install
npm run dev
```

Without the `demo` profile the backend uses PostgreSQL at `localhost:5432` (database, user and password: `sleeptracker`). Override with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`.

## Test

```sh
cd backend && ./mvnw verify                          # needs Docker for the PostgreSQL tests
cd frontend && npm run lint && npm test && npm run build
```

Coverage report: `backend/target/site/jacoco/index.html`

## Build the image only

```sh
docker build -t sleep-tracker .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=demo sleep-tracker
```

## Publish to Hugging Face Spaces

```sh
hf auth login
deploy/publish_space.sh <hf-username>/<space-name>
```

## Notes

- Garmin import: export your sleep CSV from Garmin Connect, then use "Import from Garmin" in the app.
- No accounts. Each browser keeps a random workspace ID.
- Not medical advice. Sleep cycle numbers are estimates.
- Started as a JavaFX group project (CPSC 233, University of Calgary) by Manavpreet Singh, Dylan Crombie and Gary Liao. Original version: tag [`javafx-v1`](https://github.com/manavpreet1809/Sleep-Tracker/tree/javafx-v1).
