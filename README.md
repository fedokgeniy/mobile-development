# Task Manager — Mobile Development Exam Project

Android application written in **Kotlin** for the Mobile Development course exam.

## Requirements Coverage

| Requirement | Implementation |
|---|---|
| Kotlin + Android Studio | ✅ |
| 2+ screens with Toolbar | ✅ 3 screens: List, Detail, Form |
| Screen orientation support | ✅ ViewModel survives config changes |
| Edge-to-Edge | ✅ TaskListFragment |
| List with 2+ object types | ✅ STUDY / PERSONAL |
| Multiple fields per object | ✅ title, description, type, priority, deadline, isDone |
| List + table view | ✅ RecyclerView with toggle |
| CRUD | ✅ Room DAO insert/update/delete/query |
| Persistent storage | ✅ Room SQLite database |

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (ViewModel + Repository)
- **Database:** Room (Jetpack)
- **Navigation:** Navigation Component (Fragments)
- **UI:** ViewBinding, Material3
- **Async:** Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/example/taskmanager/
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── TaskDao.kt
│   │   └── entity/
│   │       └── TaskEntity.kt
│   └── repository/
│       └── TaskRepository.kt
├── model/
│   └── TaskType.kt
├── ui/
│   ├── list/
│   │   ├── TaskListFragment.kt
│   │   └── TaskAdapter.kt
│   ├── detail/
│   │   └── TaskDetailFragment.kt
│   └── form/
│       └── TaskFormFragment.kt
└── viewmodel/
    ├── TaskListViewModel.kt
    ├── TaskDetailViewModel.kt
    └── TaskFormViewModel.kt
```

## Screens

1. **TaskListFragment** — list/table toggle, FAB to add, Edge-to-Edge enabled
2. **TaskDetailFragment** — full task details, Edit and Delete actions
3. **TaskFormFragment** — create/edit task, type selector locked on edit

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device (API 26+)
