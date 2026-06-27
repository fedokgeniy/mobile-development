# Task Manager

Android application written in Kotlin for the Mobile Development course exam.

The app allows users to create, view, edit, and delete tasks. Each task belongs
to one of two fixed types — **Study** or **Personal** — which is chosen at
creation and cannot be changed during editing. All data is stored persistently
in a local SQLite database using Room, so task information survives application
restarts and process termination.

---

## Requirements Coverage

| Requirement | Implementation |
|---|---|
| Android Studio project | app module with standard Gradle setup |
| Kotlin | 100% Kotlin, no Java |
| 2+ screens with Toolbar and title | 4 screens: Dashboard, List, Detail, Form |
| Optional Back navigation | provided by NavController via ActionBar |
| Screen orientation support | ViewModel survives configuration changes |
| Edge-to-Edge | TaskListFragment applies WindowInsets padding |
| List of objects with 2+ fixed types | STUDY and PERSONAL |
| Multiple fields per object | title, description, type, priority, deadline, isDone |
| List view and table view | RecyclerView list; table mode toggled via menu |
| Select object and view details | tap any row to open TaskDetailFragment |
| CRUD | insert, update, delete via Room DAO |
| Persistent storage | Room (SQLite) — data survives process kill |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM — ViewModel + Repository |
| Database | Room (Jetpack) |
| Navigation | Navigation Component (single-Activity, Fragment-based) |
| UI binding | ViewBinding |
| UI components | Material3 (Toolbar, FAB, RecyclerView, Spinner, SeekBar) |
| Async | Kotlin Coroutines + StateFlow / Flow |

---

## Screens

### 1. DashboardFragment (home screen)

Displays three live counters — total tasks, done, and pending — computed from
the same `StateFlow` used by the list screen. Provides a button to navigate to
the task list.

### 2. TaskListFragment (Screen 1 — Edge-to-Edge)

- Shows all tasks in a `RecyclerView` ordered by creation date, newest first.
- Applies system bar insets via `ViewCompat.setOnApplyWindowInsetsListener` so
  content is never hidden behind the status bar or navigation bar.
- Shows an empty-state message when no tasks exist.
- FAB opens the form in **create mode** (taskId = -1).
- Tapping a row navigates to the detail screen.
- Swipe-to-delete or the delete button on each row removes the task immediately.

### 3. TaskDetailFragment (Screen 2)

- Displays all fields of the selected task: title, description, type, priority,
  deadline, and completion status.
- **Edit** button navigates to the form in **edit mode** with the task ID.
- **Delete** button removes the task and pops the back stack.
- Back navigation is handled automatically by the ActionBar wired to NavController
  in `MainActivity`.

### 4. TaskFormFragment (Screen 3)

- Shared screen for creating and editing tasks.
- In **create mode**: all fields are blank; the type spinner is enabled.
- In **edit mode**: fields are pre-filled from the database; the type spinner is
  **disabled** — the task type is fixed after creation and cannot be changed.
- Title field shows an inline validation error if left empty.
- Priority is selected with a SeekBar (1 = Low, 2 = Medium, 3 = High).
- Deadline is a free-text field (yyyy-MM-dd format) and is optional.

---

## Data Model

```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    val id: Long,           // auto-generated primary key
    val title: String,
    val description: String,
    val type: String,       // TaskType.name — fixed at creation
    val priority: Int,      // 1 (Low), 2 (Medium), 3 (High)
    val deadline: String?,  // ISO-8601 date or null
    val isDone: Boolean,
    val createdAt: Long     // Unix timestamp in milliseconds
)
```

```kotlin
enum class TaskType(val displayName: String) {
    STUDY("Study"),
    PERSONAL("Personal")
}
```

The `type` field is stored as the enum constant name (e.g., `"STUDY"`). The
`TaskRepository.update` method re-reads the stored type from the database before
calling `TaskDao.update`, so even if a caller accidentally passes a different
type string it will be silently corrected.

---

## Architecture

```
UI (Fragment)
    |  observes StateFlow
    v
ViewModel (AndroidViewModel)
    |  calls suspend functions
    v
Repository
    |  delegates to
    v
TaskDao (Room)
    |  reads / writes
    v
Room SQLite database  (task_manager.db)
```

- **ViewModels** hold no reference to the Fragment or Context (except
  `AndroidViewModel` which keeps `Application`). They survive rotation.
- **Repository** is the single source of truth. ViewModels never call the DAO
  directly.
- **Flow** is used for reactive reads. `StateFlow` with `WhileSubscribed(5000)`
  keeps the upstream Flow active for 5 seconds after the last subscriber drops,
  which covers the common rotation scenario without wasting resources.
- **`_binding = null`** in `onDestroyView` prevents memory leaks caused by
  ViewBinding holding a reference to the destroyed view hierarchy.

---

## Project Structure

```
app/src/main/java/com/example/taskmanager/
├── MainActivity.kt                    # single Activity, NavHost, ActionBar setup
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt             # Room database singleton
│   │   ├── TaskDao.kt                 # DAO — all SQL queries
│   │   └── entity/
│   │       └── TaskEntity.kt          # Room entity / data class
│   └── repository/
│       └── TaskRepository.kt          # single source of truth
├── model/
│   └── TaskType.kt                    # STUDY / PERSONAL enum
├── ui/
│   ├── dashboard/
│   │   └── DashboardFragment.kt       # home screen with task stats
│   ├── list/
│   │   ├── TaskListFragment.kt        # Edge-to-Edge list screen
│   │   └── TaskAdapter.kt             # ListAdapter with DiffUtil
│   ├── detail/
│   │   └── TaskDetailFragment.kt      # read-only detail + edit/delete
│   └── form/
│       └── TaskFormFragment.kt        # create / edit form
└── viewmodel/
    ├── TaskListViewModel.kt           # exposes StateFlow<List<TaskEntity>>
    ├── TaskDetailViewModel.kt         # loads single task by ID
    └── TaskFormViewModel.kt           # handles create/edit save logic
```

---

## Setup

1. Clone the repository.
2. Open in Android Studio (Hedgehog or newer recommended).
3. Let Gradle sync finish automatically.
4. Run on an emulator or physical device running **API 26 (Android 8.0) or higher**.

No API keys, network permissions, or additional configuration are required.
The app works fully offline.
