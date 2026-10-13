# Sleep Tracker

Log sleep, import Garmin CSV files, and explore sleep trends and daily habits.

**Built with:** Java, Spring Boot, PostgreSQL, React, and TypeScript.

## Preview

![Sleep Tracker dashboard](docs/images/dashboard.png)

## Highlights

- **Sleep history:** enter nights manually or import an exported Garmin CSV.
- **Trends:** view sleep summaries and changes over time.
- **Habit comparisons:** explore associations between daily habits and sleep without treating them as causes.

## Project structure

```text
backend/    Spring Boot API, analytics, and database migrations
frontend/   React interface and charts
deploy/     Deployment scripts
docs/       Project documentation and screenshots
```

## Run locally

With Docker installed:

```sh
docker compose up --build
```

Open [localhost:8080](http://localhost:8080).
API documentation is at [localhost:8080/api/docs](http://localhost:8080/api/docs).
The included database credentials are for local development only.

## Tests

Backend (requires Java 25 and Docker):

```sh
cd backend
./mvnw verify
```

Frontend (requires Node.js 24; run from the project root in another terminal):

```sh
cd frontend
npm install
npm test
```

## Notes and credits

There are no user accounts. Sleep estimates are not medical advice.

Started as a CPSC 233 JavaFX group project by Manavpreet Singh, Dylan Crombie,
and Gary Liao. The original is preserved in [javafx-v1](https://github.com/manavpreet1809/Sleep-Tracker/tree/javafx-v1).
