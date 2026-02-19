# Jojo Watch by Joshmode

A simple JavaFX application for tracking pet vitals, routines, and food intake. Created for fun by joshmode for his beloved dog Jojo <3

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

- Data is erased at shutdown. Will create an SQL database for future plans. 
- Daily food tracking resets automatically based on the date.
