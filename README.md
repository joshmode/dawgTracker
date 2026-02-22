### `dawgTracker` 
# 🐾 dawgTracker: Biometric Data Logging Engine
```markdown
## A Java-based application featuring a custom graphical user interface (GUI), designed to persistently log time-series health metrics and compute predictive anomaly indices.  ## Data Ingestion Architecture  ```text [ GUI Client ] ──► Inputs: Vitals, Weight, Grooming, Food        │        ▼ ( Local Data Store ) ──► Persists time-series health logs        │        ▼ ( Analytic Engine ) ──► Processes 'Scratch Index' Algorithm        │        ▼ [ Alert System ] ──► Flags potential allergy anomalies 
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
