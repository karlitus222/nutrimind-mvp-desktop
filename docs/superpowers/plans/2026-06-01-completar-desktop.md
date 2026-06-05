# Nutrimind Desktop Completion Implementation Plan

> **For agentic workers:** historical plan kept for traceability. The current delivery decision is: desktop Java is the main system, web remains as a demo/support app.

**Goal:** Complete a demonstrable Java 17 + Swing + SQLite desktop system with patients, consultations, AI analysis, alerts, reports, meal plans and administration.

**Architecture:** Java 17 + Swing + SQLite with MVC, DAO and Singleton connection. The real desktop AI flow now uses Gemini as the main provider when `GEMINI_API_KEY` exists and OpenAI as fallback when `OPENAI_API_KEY` exists.

**Tech Stack:** Java 17, Swing, SQLite JDBC, PowerShell, Gemini API and OpenAI API.

**Execution status:** completed and later updated to keep the web app as support while making the desktop the main academic delivery.

## Completed Work

- Build scripts fail correctly when `javac` fails.
- SQLite schema and seed data are initialized by the desktop.
- Models, DAOs, controllers, services and Swing views are organized by MVC.
- Login, patients, consultations, alerts, reports, meal plans and administration are present.
- Smoke tests validate the desktop flow with simulated IA.
- Gemini services were added to transfer the working web IA approach to the desktop.
- OpenAI remains as fallback.
- Documentation was realigned to Java desktop as the main delivery.
