# Sleep Tracker

[![CI](https://github.com/manavpreet1809/Sleep-Tracker/actions/workflows/ci.yml/badge.svg)](https://github.com/manavpreet1809/Sleep-Tracker/actions/workflows/ci.yml)

A web app for logging your sleep. You enter when you slept, how well you slept, and a few habits like screen time or exercise. The app then shows your trends, how regular your schedule is, and which habits line up with your better nights. You can also import sleep data exported from Garmin Connect.

![Sleep Tracker dashboard](docs/images/dashboard.png)

## What it does

- Log a night: date, quality (1 to 10), bedtime, wake time, hours asleep, and optional habits.
- Charts for sleep quality, sleep duration, and bedtime/wake time over time.
- A consistency score out of 100 for how regular your schedule is.
- Compares nights with and without each habit, for example nights with 4 hours or less of screen time against nights with more.
- Predicts tonight's sleep quality from your past nights once you have at least 7.
- Imports a Garmin Connect sleep CSV. Sleep score, times, resting heart rate and HRV are added, and habits you typed in yourself are kept.
- No accounts. Each browser gets its own random ID, so people trying the demo don't see each other's data.

The habit comparisons show patterns in your own data, not proof that a habit causes better sleep. Sleep cycle numbers are rough estimates. None of this is medical advice.

## Built with

Java 25 and Spring Boot 4 for the backend, PostgreSQL with Flyway for the database, and React with TypeScript for the frontend. Tests use JUnit, Testcontainers and Vitest. It runs in Docker, and GitHub Actions runs the tests on every push.

## Running it

You only need Docker:

```sh
docker compose up --build
```

Then open http://localhost:8080. Click "Try it with 60 example nights" to see it with sample data. The API documentation is at http://localhost:8080/api/docs.

To work on the code instead:

```sh
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=demo   # API on port 8080, needs Java 25
cd frontend && npm install && npm run dev                               # site on port 5173, needs Node 24
```

The `demo` profile uses an in-memory database, so nothing is saved after you stop it. Without it, the backend connects to PostgreSQL on localhost:5432 (database, user and password are all `sleeptracker`).

## Tests

```sh
cd backend && ./mvnw verify
cd frontend && npm run lint && npm test
```

The backend tests start a real PostgreSQL database in Docker for the API tests.

## How the numbers are worked out

- **Consistency:** how much your bedtime, wake time and sleep length vary from night to night. Times are compared on a 24-hour clock, so 11:30 pm and 12:30 am count as one hour apart.
- **Habits:** the average quality (or hours) on nights you followed a habit, compared with nights you didn't. Nights where you didn't record that habit are left out.
- **Prediction:** a ridge regression fitted to your own nights, using the habits you record most often. The result is kept between 1 and 10.

## History

This started as a JavaFX desktop app for CPSC 233 at the University of Calgary, made by Manavpreet Singh, Dylan Crombie and Gary Liao. That version is saved under the [`javafx-v1`](https://github.com/manavpreet1809/Sleep-Tracker/tree/javafx-v1) tag.

I rebuilt it as a web app. The main ideas from the original are still here: averaging bedtimes across midnight, the consistency score, the habit thresholds and the sleep cycle estimate. While rebuilding, I fixed three problems in the original:

- The habit analysis counted every good night for every habit. It now compares nights with and without each habit.
- Waking up at 11:30 and at 12:30 looked 23 hours apart in the consistency score. Times are now compared around the clock.
- The quality prediction used hand-picked multipliers. It now fits a model to your own data.
