# AEGIS — Adaptive Executive & General Intelligence System

**AEGIS** is a security-first, multi-domain AI agent built for Android with Kotlin, Jetpack Compose, Room, and Gemini AI integration. AEGIS operates across multiple domains — Security, Data Analytics, Symbolic Math, Art & Vision, Sales & Revenue, Health Analytics, and Executive Task Management — while enforcing strict input sanitization, threat screening, and source traceability.

---

## 🌟 Key Features

- **Security & Defense Engine**:
  - Automatic PII sanitization (redacts emails, phone numbers, and SSNs before sending queries externally).
  - Direct SQL injection screening and threat gating.
  - Complete security audit log tracked in real-time via a local Room database.
- **Multi-Domain Intelligence**:
  - **Security & Defense**: Threat screening, encryption status, and compliance logs.
  - **Data Analytics**: Tabular analytics using schema mappings for BigQuery public datasets.
  - **Math & Logic**: Step-by-step symbolic math transformations and reasoning.
  - **Art & Vision**: Metropolitan Museum Vision API vectors and aesthetic design guidelines.
  - **Sales & Revenue**: Transparent regional revenue forecasts and deal velocity metrics.
  - **Health & Targets**: Clinical target prioritisation scores (Open Targets) and CMS enrollment metrics.
  - **Executive Assistant**: Directives, tasks, priorities, and workflow organization.
- **Parallel Source Traceability**:
  - Searches up to 3 domain-mapped external datasets in parallel before generating responses.
  - Displays source attribution and confidence scores for every answer.
- **Firebase Authentication & Credential Manager**:
  - Support for Google Sign-In via Android Credential Manager.
  - Email/Password authentication flow.
  - Executive Guest (Anonymous) clearance mode.
- **Offline Persistence & Local Storage**:
  - Built with Room Database (`AegisDatabase`) to persist chat messages, audit logs, and executive directives.

---

## 🚀 Getting Started

### Requirements
- Android 7.0 (API level 24) or higher.
- Android Studio Ladybug / Jellyfish or latest AGP.
- JDK 11 or higher.

### API Key Configuration
AEGIS connects to Gemini AI using the official REST API.
1. Obtain a Gemini API Key from Google AI Studio.
2. Open the **Secrets panel** or set your key in `.env`:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key
   ```
3. If no key is configured, AEGIS seamlessly operates using its built-in offline intelligence engine and parallel local source search.

---

## 📖 User Instructions

### 1. Security Clearance & Authentication
- Tap the **Lock / Profile Icon** in the top-right header to manage your security clearance.
- Choose between **Google Sign-In** (Credential Manager), **Email/Password**, or **Executive Guest** clearance.

### 2. Multi-Domain Prompting
- Use the domain bar at the top to filter domains, or simply type your request in natural language.
- AEGIS automatically detects the underlying domain and routes queries to the appropriate parallel sources.

### 3. Executive Directive Management
- Tap the **Tasks** badge in the header to view, add, toggle, or delete executive tasks and priorities.

### 4. Viewing Security Audit Logs
- Tap the **Shield / Verified User Icon** in the top header to inspect sanitization logs, PII redactions, and parallel source calls for full transparency.

---

## 🛠 Tech Stack

- **UI Framework**: Jetpack Compose (Material Design 3)
- **Language**: Kotlin
- **Database**: Room Database (SQLite)
- **Networking**: Retrofit 2 + OkHttp + Moshi
- **Authentication**: Firebase Auth + AndroidX Credential Manager + Google Identity
- **AI Integration**: Gemini REST API (`gemini-3.5-flash`)
