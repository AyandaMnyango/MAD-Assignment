# Smart Pantry Manager

## App Description
Smart Pantry Manager is a native Android application built entirely in Java. It is designed to help users reduce household food waste by tracking the ingredients they currently have in their pantry and strictly matching them to recipes they can cook right now. 

The app features a core "Strict-Matching Algorithm" that ensures a recipe is only suggested if the user possesses **every single required ingredient** in the correct quantity. It also includes a bonus "Almost There" feature that highlights recipes where the user is only missing exactly one ingredient.

## Features Implemented
*   **Full CRUD Functionality:** Users can Create, Read, Update (Edit), and Delete pantry items.
*   **Strict-Matching Logic:** Relational database queries ensure no partial matches are suggested.
*   **Data Normalization:** Handles pluralization (e.g., matching "tomatoes" to "tomato") and case-sensitivity.
*   **Persistent Storage:** Data survives app closures using local storage.
*   **User Interface:** Uses a Bottom Navigation View to navigate between Fragments (Pantry, Recipes, Settings), and custom RecyclerViews with AlertDialogs for item management.

## Database Choice
**SQLite (via `SQLiteOpenHelper`)**
I chose SQLite for this application because it is a native, offline-first solution. Unlike Firebase or PostgreSQL, it requires no internet connection, no external API keys, and no backend hosting. Because the Smart Pantry Manager needs to work instantly in a user's kitchen regardless of their Wi-Fi connection, a local relational database is the most reliable and secure choice. It also allows for robust relational mapping between the `recipes` table and the `recipe_ingredients` table using Foreign Keys.

## Setup & Run Instructions
To run this project locally, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AyandaMnyango/MAD-Assignment.git

2. Open in Android Studio:
  Open Android Studio, select File > Open, and navigate to the cloned project folder.

3. Gradle Sync:
  Allow Android Studio to sync the project with Gradle files. Ensure you are connected to the internet for the initial dependency download.

4. Emulator Requirements:
  The app requires a minimum SDK of API 26 (Android 8.0). Ensure you have an emulator set up with API 26 or higher.

5. Run the App:
  Click the green Run (▶) button in Android Studio.

Testing Notice (Pre-seeded Data)
To make testing and marking easier, the database is pre-seeded on first run.

The app will automatically populate the pantry with 10 common ingredients (e.g., eggs, cheese, bread, pasta).

The database includes 20 predefined recipes.

You can immediately tap the Recipes tab to see the strict-matching logic and the "Almost There" list in action without having to manually add items first.

Author
Name:Ayanda Mnyango

Student Number: 402311034

Module: Mobile App Development 700
