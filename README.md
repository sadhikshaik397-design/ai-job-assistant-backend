# AI Job Assistant — Backend

Spring Boot REST API for an AI-powered job search assistant. It connects to MySQL, searches saved jobs and Adzuna, compares jobs with a user's profile, and supports application tracking.

## Requirements

- Java 21
- MySQL 8.4
- Ollama running locally, with the `qwen2.5:0.5b` model
- Adzuna API credentials for external job search (optional)

## Configuration

The application reads credentials from environment variables. **Never put real passwords or API keys in `application.properties` or commit them to GitHub.**

Create the database in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS job_assistant;
```

In PowerShell, set the required database password and optional service credentials:

```powershell
$env:DB_PASSWORD = Read-Host "Enter your MySQL password"
$env:ADZUNA_APP_ID = "your_adzuna_app_id"
$env:ADZUNA_APP_KEY = "your_adzuna_app_key"
$env:GMAIL_USERNAME = "your_email@gmail.com"
$env:GMAIL_APP_PASSWORD = "your_gmail_app_password"
```

Replace the example values with your own. Keep these values private. The Adzuna and Gmail variables are optional if you are not using those features.

Optional settings:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/job_assistant"
$env:DB_USERNAME = "root"
$env:OLLAMA_BASE_URL = "http://localhost:11434"
$env:OLLAMA_MODEL = "qwen2.5:0.5b"
```

## Run the backend

Open PowerShell in the backend project folder—the folder containing `pom.xml`—then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

Keep the PowerShell window open while using the frontend. The environment variables set in PowerShell are available only in that window.

## Run the frontend

Start the React frontend in a separate PowerShell window. See the frontend project's `README.md` for its setup and run instructions.

## Main features

- User registration and login
- User profile management
- Job search with saved jobs and optional Adzuna results
- Job skill matching and skill-gap display
- AI job analysis and tailored resume generation through Ollama
- Application tracking and status management

## Authentication note

The local portfolio version uses bearer tokens held in memory by the backend. Restarting the backend invalidates existing login tokens, so log in again after a restart. This setup is intended for a local demo and should be reviewed before public production deployment.

## Security

- Keep `.env` files, passwords, API keys, and other credentials out of version control.
- The backend `.gitignore` should exclude `.env` and the Maven `target/` directory.
- Do not commit real credentials in screenshots, logs, or documentation.

## License

Add a license before redistributing this project.