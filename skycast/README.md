# Skycast

A weather app with a **Spring Boot** API and a **React** frontend. Search any city to see current conditions and a 7-day forecast. The page color changes with the weather and time of day.

Weather data comes from [Open-Meteo](https://open-meteo.com/), which is free and needs no API key.

## How it works

```
React (Vite, :5173)  ──/api/weather──▶  Spring Boot (:8080)  ──▶  Open-Meteo geocoding + forecast
```

- The backend looks up the city's coordinates, fetches the forecast, and returns one compact JSON response.
- Results are cached for 10 minutes per city and unit system (Caffeine).
- Errors are returned as RFC 7807 `problem+json`; the frontend shows the `detail` text.
- In development, Vite proxies `/api` to the backend, so no CORS setup is needed.

## Project layout

```
skycast/
├── backend/    Spring Boot 3, Java 17, Maven
│   └── src/main/java/com/skycast/
│       ├── client/       Open-Meteo HTTP client
│       ├── config/       caching, CORS, typed properties
│       ├── controller/   GET /api/weather
│       ├── exception/    domain exceptions + global handler
│       ├── model/        API and upstream DTOs (records), WMO weather codes
│       └── service/      orchestration + caching
├── frontend/   React 18 + Vite
└── .github/workflows/ci.yml
```

## Requirements

- Java 17+
- Maven 3.9+
- Node.js 18+

## Run locally

Start the backend:

```bash
cd backend
mvn spring-boot:run
```

In a second terminal, start the frontend:

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173.

## API

`GET /api/weather?city=Lisbon&units=metric`

| Param   | Required | Values                    |
|---------|----------|---------------------------|
| `city`  | yes      | up to 100 characters      |
| `units` | no       | `metric` (default), `imperial` |

Example response (shortened):

```json
{
  "location": { "name": "Lisbon", "region": "Lisbon", "country": "Portugal", "latitude": 38.72, "longitude": -9.14 },
  "current": {
    "temperature": 21.5, "feelsLike": 20.0, "humidity": 60, "windSpeed": 12.0,
    "daytime": true,
    "condition": { "code": 2, "label": "Partly cloudy", "category": "cloudy" }
  },
  "daily": [
    { "date": "2026-11-04", "min": 14.0, "max": 23.0, "precipitationChance": 5,
      "condition": { "code": 2, "label": "Partly cloudy", "category": "cloudy" } }
  ],
  "units": { "temperature": "°C", "wind": "km/h" }
}
```

| Status | Meaning |
|--------|---------|
| 400    | Blank or too-long city, or unknown units |
| 404    | City not found |
| 502    | Open-Meteo did not respond |

## Tests

```bash
cd backend && mvn verify
```

Covers weather-code mapping, the service layer (mocked client), and the controller (MockMvc), including error responses.

## Configuration

Backend settings are in `backend/src/main/resources/application.properties`:

| Property                       | Default                  |
|--------------------------------|--------------------------|
| `weather.api.timeout-seconds`  | `5`                      |
| `weather.cors.allowed-origins` | `http://localhost:5173`  |
| `spring.cache.caffeine.spec`   | `maximumSize=500,expireAfterWrite=10m` |

If you host the frontend on a different origin than the API, set `VITE_API_BASE` (see `frontend/.env.example`) and add that origin to `weather.cors.allowed-origins`.

## Ideas for next steps

- Hourly forecast chart
- City autocomplete using the geocoding API's `count` parameter
- Dockerfile and `docker-compose.yml` for one-command startup
- Frontend tests with Vitest and React Testing Library

## License

MIT
