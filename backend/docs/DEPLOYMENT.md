# Deployment Guide

## Local Development

`ash
cp .env.example .env
# Fill in DATABASE_URL and JWT_SECRET in .env

./gradlew bootRun
`

The backend runs on http://localhost:8080. Flyway applies migrations automatically.

## Docker Compose (local full stack)

`yaml
version: '3.9'
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: sipdb
      POSTGRES_USER: sipuser
      POSTGRES_PASSWORD: devpassword
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      DATABASE_URL: jdbc:postgresql://db:5432/sipdb
      DATABASE_USERNAME: sipuser
      DATABASE_PASSWORD: devpassword
      JWT_SECRET: your-secret-key-at-least-32-characters-long
    depends_on:
      - db

volumes:
  pgdata:
`

## Neon PostgreSQL Setup

1. Create a project at [Neon](https://neon.tech)
2. Create a development branch
3. Copy the connection string from the dashboard
4. Set DATABASE_URL in your .env or deployment config

`ash
# Example DATABASE_URL
DATABASE_URL=jdbc:postgresql://user:password@ep-xxx-123456.us-east-2.aws.neon.tech/sipdb?sslmode=require
`

## Render (staging)

1. Create a Web Service
2. Set build command: ./gradlew bootJar
3. Set start command: java -jar build/libs/sip-backend-0.1.0.jar
4. Add environment variables:
   - DATABASE_URL (Neon development branch)
   - JWT_SECRET (generate with openssl rand -base64 64)

> **Note:** Render free tier spins down after 15 min of inactivity. Use a paid plan or always-on host for production.

## Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| DATABASE_URL | Yes | — | Full JDBC connection string |
| DATABASE_USERNAME | No | sipuser | Override DB username |
| DATABASE_PASSWORD | No | — | Override DB password |
| JWT_SECRET | Yes | — | JWT signing key (min 32 chars) |
| JWT_ACCESS_EXPIRY | No | 900 | Access token TTL in seconds |
| JWT_REFRESH_EXPIRY | No | 604800 | Refresh token TTL in seconds |
| SERVER_PORT | No | 8080 | HTTP server port |