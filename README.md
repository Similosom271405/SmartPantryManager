# Smart Pantry Manager

A native Java Android application built to eliminate household food waste by suggesting recipes strictly based on leftover ingredients.

---

## 1. Core Value Proposition: The Strict-Matching Rule
Unlike traditional cooking applications that generate missing-ingredient grocery lists, the Smart Pantry Manager strictly surfaces recipes where **100% of the required ingredients are present in the user's pantry in sufficient quantity**. If even one ingredient is missing or below the required measurement, the recipe is excluded from suggestions.

## 2. Database Choice & Justification
- **Selected Engine**: SQLite using `SQLiteOpenHelper`.
- **Architectural Justification**:
    1. **Zero Latency & Offline Availability**: Pantry tracking occurs in kitchens where internet access may be unstable. Storing data locally ensures instant access and editing without network roundtrips.
    2. **No Cloud Credentials or Billing**: Avoids third-party API dependencies, backend downtime, or cloud quota limitations.
    3. **Genuine Persistence**: Ensures that all pantry items and seeded recipes persist reliably across device restarts and app terminations.

## 3. Application Architecture & Screens
1. **Pantry List Screen**: Dynamic inventory management via `RecyclerView` with full CRUD capabilities.
2. **Add / Edit Ingredient Screen**: Input form with validation, unit dropdown, and date picker.
3. **Suggested Recipes Screen**: Evaluates the strict-matching engine against current pantry stock.
4. **Recipe Detail Screen**: Displays required ingredient checklists and step-by-step cooking steps.
5. **Settings Screen**: User preferences for expiring-soon alerts and measurement system selection.

## 4. Setup and Run Instructions
1. Open **Android Studio** (Hedgehog 2023.1.1 or newer).
2. Select **File > Open** and select the `SmartPantryManager` root folder.
3. Allow Gradle to sync dependencies (`MaterialComponents`, `RecyclerView`, `CardView`).
4. Select an Android Emulator running **API 24 (Nougat)** or higher.
5. Click the green **Run (Shift + F10)** button.