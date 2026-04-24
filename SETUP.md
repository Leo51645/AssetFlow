# AssetFlow Setup Guide

## Voraussetzungen
- Java 21+
- Maven 3.8+
- PostgreSQL 13+ (nur für Local Dev, Tests nutzen H2)

## Local Development Setup

### 1. `.env` Datei erstellen
```bash
cp .env.example .env
```
Dann `.env` mit deinen echten DB-Credentials füllen:
```dotenv
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_NAME=assetflow
JWT_SECRET_KEY=your_base64_secret_key
```

### 2. PostgreSQL Datenbank erstellen
```sql
CREATE DATABASE assetflow;
```

### 3. App starten (Local Profile)
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```
→ Läuft auf `http://localhost:8080`

## Tests ausführen
```bash
./mvnw clean test
```
→ Nutzt H2 In-Memory, keine PostgreSQL nötig ✅

## Production Build
```bash
./mvnw clean package
java -jar target/AssetFlow-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

## Profile Übersicht

| Profile | Datenbank | Nutzung |
|---------|-----------|---------|
| `default` (prod) | PostgreSQL | Production |
| `local` | PostgreSQL | Local Development |
| `test` | H2 Memory | Automated Tests |

## Security

⚠️ **WICHTIG:**
- `.env` ist in `.gitignore` — wird NICHT committed!
- Nutze `.env.example` als Template für neue Developer
- Secrets NIEMALS in Code hardcoden
- JWT_SECRET_KEY muss min. 44 Zeichen (BASE64, 256-Bit) sein

## Troubleshooting

**PostgreSQL Fehler?**
```bash
docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:latest
```

**Tests schlagen fehl?**
```bash
./mvnw clean compile
./mvnw test -o  # offline mode
```

---
Erstellt: 2026-04-24

