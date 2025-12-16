# Habit Tracker - Android Wellness App

A comprehensive Android wellness application that helps users manage their daily health routines, track mood, and maintain healthy habits.

## Features

### Core Requirements ✅
- **Daily Habit Tracker**: Add, edit, delete daily wellness habits with completion tracking
- **Mood Journal**: Log mood entries with emoji selector and date/time stamps
- **Hydration Reminder**: WorkManager-based notifications to remind users to drink water
- **Advanced Features**: Home-screen widget, MPAndroidChart integration, and sensor support

### Technical Implementation ✅
- **Architecture**: Fragment-based navigation with MVVM pattern
- **Data Persistence**: SharedPreferences for storing user data
- **Intents**: Implicit/explicit intents for navigation and sharing
- **State Management**: Retains user settings across sessions
- **Responsive UI**: Adapts to phones and tablets in portrait & landscape

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/habbittracker/
│   │   ├── data/                    # Data models
│   │   │   ├── Habit.kt
│   │   │   └── MoodEntry.kt
│   │   ├── ui/                       # UI components
│   │   │   ├── habits/               # Habit tracking
│   │   │   ├── mood/                 # Mood journal
│   │   │   ├── charts/               # Analytics & charts
│   │   │   └── settings/             # App settings
│   │   ├── utils/                    # Utilities
│   │   │   └── DataManager.kt       # Data persistence
│   │   ├── work/                     # WorkManager
│   │   │   └── HydrationReminderWorker.kt
│   │   ├── widget/                   # Home screen widget
│   │   │   └── HabitWidgetProvider.kt
│   │   └── receiver/                 # Broadcast receivers
│   │       └── BootReceiver.kt
│   ├── res/
│   │   ├── layout/                   # UI layouts
│   │   ├── drawable/                 # Icons and graphics
│   │   ├── values/                   # Strings, colors, themes
│   │   └── xml/                      # Widget and backup configs
│   └── AndroidManifest.xml
```

## Key Components

### 1. Daily Habit Tracker
- **HabitsFragment**: Main UI for habit management
- **HabitsViewModel**: Business logic and data handling
- **HabitsAdapter**: RecyclerView adapter for habit list
- Features: Add/edit/delete habits, mark completion, progress tracking

### 2. Mood Journal
- **MoodFragment**: Mood logging interface
- **MoodViewModel**: Mood data management
- **MoodAdapter**: Display mood entries
- Features: Emoji selection, note taking, date/time logging, sharing

### 3. Hydration Reminder
- **HydrationReminderWorker**: WorkManager for notifications
- **SettingsFragment**: Configuration interface
- Features: Customizable intervals, persistent notifications

### 4. Charts & Analytics
- **ChartsFragment**: Data visualization
- **ChartsViewModel**: Chart data processing
- Features: Mood trends, habit completion charts using MPAndroidChart

### 5. Home Screen Widget
- **HabitWidgetProvider**: Widget implementation
- Features: Today's progress display, click to open app

## Dependencies

```gradle
implementation "androidx.core:core-ktx:1.12.0"
implementation "androidx.appcompat:appcompat:1.6.1"
implementation "com.google.android.material:material:1.10.0"
implementation "androidx.constraintlayout:constraintlayout:2.1.4"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.navigation:navigation-fragment-ktx:2.7.5"
implementation "androidx.navigation:navigation-ui-ktx:2.7.5"
implementation "androidx.work:work-runtime-ktx:2.9.0"
implementation "com.github.PhilJay:MPAndroidChart:v3.1.0"
implementation "androidx.preference:preference-ktx:1.2.1"
```

## Data Storage

The app uses SharedPreferences for data persistence:
- **Habits**: Stored as JSON in SharedPreferences
- **Mood Entries**: Serialized mood data with timestamps
- **Settings**: Hydration reminder preferences
- **Completions**: Daily habit completion tracking

## Advanced Features

### 1. Home Screen Widget
- Displays today's habit completion percentage
- Updates automatically
- Click to open main app

### 2. MPAndroidChart Integration
- Mood trend visualization over 7 days
- Habit completion percentage charts
- Interactive and responsive charts

### 3. WorkManager Notifications
- Periodic hydration reminders
- Customizable intervals (minimum 15 minutes)
- Persistent across app restarts

## Permissions

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

## Installation & Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

## Testing

The app has been designed with:
- **Responsive UI**: Works on phones and tablets
- **Orientation Support**: Portrait and landscape modes
- **Data Persistence**: Settings and data survive app restarts
- **Notification Testing**: Hydration reminders work reliably

## Code Quality

- **Clean Architecture**: MVVM pattern with separation of concerns
- **Documentation**: Comprehensive comments and documentation
- **Error Handling**: Proper error handling and user feedback
- **Performance**: Efficient data management and UI updates

## Future Enhancements

- Database integration (Room)
- Cloud sync capabilities
- Advanced analytics
- Social features
- Custom themes
- Export/import functionality

## Screenshots

The app includes:
- Modern Material Design UI
- Intuitive navigation with bottom tabs
- Progress tracking with visual indicators
- Interactive charts and analytics
- Home screen widget integration

## License

This project is developed as an academic assignment demonstrating Android development skills and best practices.
