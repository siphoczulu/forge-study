# Forge Study

Forge is a lightweight study dashboard + habit system built in Java.
- **v0.1:** Java CLI + local JSON persistence
- **v0.2 (in progress):** JavaFX desktop GUI on top of the same core + same `forge_data.json`

## Features (shipped)
### Courses & Topics
- Create/list/delete courses
- Create/list topics per course

### Study Sessions
- Log a study session (date, course, topic, duration, notes)
- Automatically updates `topic.lastStudied`

### Deadlines
- Add/list deadlines (sorted by due date)
- Optional weight field

### Dashboard (logic shipped)
- “What to study today”: per course, recommends the topic with the oldest `lastStudied` (or never studied)
- “Weekly target”: per course, checks if at least one study session happened this week (Mon → today)

## Persistence
All data is saved locally to:
- `forge_data.json`

> Note: `forge_data.json` is **ignored by git** (personal data).

## How to run

### JavaFX app (v0.2 shell)
```bash
./gradlew run