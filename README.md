# 🐾 dawgTracker: Biometric Data & Analytics Engine

> A Java-based telemetry application featuring a custom graphical user interface (GUI), engineered to persistently log health metrics and compute predictive anomaly indices.

## Data Ingestion & Processing Flow

```text
[ Java GUI Client ] ──► Captures Vitals, Weight, Food & Grooming Logs
         │
         ▼
( Local Data Store) ──► Persists time-series health telemetry
         │
         ▼
[ Analytic Engine ] ──► Processes 'Scratch Index' Algorithm
         │
         ▼
( Anomaly Output  ) ──► Flags potential allergic reactions


⚙️ Core Engineering
	•	Time-Series Data Management: Engineered robust logging mechanisms to persistently track daily metrics over extended periods.
	•	Predictive Analytics: Developed a unique "Scratch Index" to analyze behavioral frequency data and proactively flag allergic reactions.
	•	Application Design: Built entirely in Java with a focus on usability and strict state management.


## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## How to Run

1.  Clone the repository.
2.  Navigate to the project root directory.
3.  Run the application using Maven:

    ```bash
    mvn javafx:run
    ```

## How to Test

Run the unit tests using:

```bash
mvn test
```

## Features

- **Vitals & Activity:** Track Heart Rate, Respiratory Rate, Active Minutes, and Scratch Index.
- **Logs & Tracking:** Log food intake (with daily totals) and weight history.
- **Routine Care:** Track and update intervals for tasks like Teeth Brushing, Ear Cleaning, Grooming, and NexGard.
- **Settings:** Customize daily goals and routine intervals.

## Notes & Limitations

- Data is erased at shutdown. Will create an SQL database for future plans. (EDIT: resolved with backend implementation)
- Daily food tracking, vitals resets automatically based on the date.
