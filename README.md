# SanNorem — Nasugbu Retail Coverage Explorer

**SanNorem** (`com.sannorem.app`) is a modern Android application built with Kotlin, Jetpack Compose, Material 3, and the Google Maps Compose SDK. It visualizes and analyzes retail coverage for **DALI Everyday Grocery**, **7-Eleven**, and **Alfamart** branches across all **42 official barangays** of Nasugbu, Batangas, Philippines.

---

## 1. Dual Map Engine (Zero API Key by Default)

SanNorem supports two map engines:

1. **OpenStreetMap (Default — Zero API Key Required):**
   - Uses `org.osmdroid:osmdroid-android` to stream live OpenStreetMap street tiles directly.
   - **No API key, no registration, no billing, and no credit card required.**
   - Includes full interactivity: pinch-to-zoom, pan, color-coded store pins, click-to-reveal service radius circles, customer pin placement, and live coverage calculation.

2. **Google Maps SDK (Optional):**
   - You can toggle between OpenStreetMap and Google Maps anytime using the **Layers** icon in the top app bar.
   - To use Google Maps tiles, add your key to `local.properties`:
     ```properties
     MAPS_API_KEY=AIzaSy...YourKeyHere
     ```


---

## 2. Features and App Flow

### Screen 1: Dashboard (Start Destination)
- **Hero Header:** Nasugbu retail exploration onboarding.
- **Metric Cards:** Total barangays (42), retail branches (23), and brands (3).
- **Brand Cards:** Branch distribution and default service radii for DALI (8), 7-Eleven (3), and Alfamart (12).
- **Exposed Dropdown Menu:** Browse and select all 42 official barangays grouped into **Poblacion** (12) and **Rural & Coastal** (30).
- **Primary Button ("View coverage map"):** Enabled once a barangay is chosen. Navigates smoothly to the map.
- **Secondary Button ("Test Customer Location"):** Requests runtime location permission (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`) and reads the device position via `FusedLocationProviderClient`.
  - If permission is denied or location is off, or if the user is located more than 20 km away from Nasugbu, it falls back to the Nasugbu Municipal Center (`14.0734, 120.6315`) and displays an explanatory Snackbar.

### Screen 2: Coverage Map
- **Initial View:** Google Map centered on the selected barangay or customer location (zoom 14–15), with 23 color-coded pins and **0 circles by default**.
- **Click-to-Reveal Service Radius:** Tapping any store pin displays a translucent circle (15% opacity, brand color) matching that store's service radius. Tapping another pin immediately swaps the circle so **at most one circle exists at a time**. Tapping empty map space or dismissing info clears the circle.
- **Customer Coverage Evaluation:** Tapping empty map space drops a Customer pin and opens a bottom sheet listing all branches whose service radius covers that point, ordered nearest-first with exact distances in meters.
- **Brand Filters:** Toggle chips for DALI, 7-Eleven, and Alfamart to show or hide store markers.
- **Legend:** Detailed service radius and brand color reference sheet.
- **Approximate Center Notice:** Displays a note when a chosen rural barangay has pending PhilAtlas center verification.

---

## 3. Data Sources & Architecture

The architecture follows strict **MVVM** with unidirectional data flow (`StateFlow`, immutable UI state) and a clean domain layer with **zero Android dependencies** for fast, deterministic JVM unit testing:

```
com.sannorem.app
 ├─ domain/
 │   ├─ model/      # RetailBranch, Brand, Barangay, BarangayGroup, CoverageResult
 │   ├─ geo/        # GeoUtils (Pure Kotlin Haversine, Earth radius = 6371.0088 km)
 │   └─ usecase/    # FindCoveringBranchesUseCase (filters distance <= radius, sorted nearest-first)
 ├─ data/           # BranchRepository & BarangayRepository interfaces + Asset implementations
 ├─ ui/             # theme/, navigation/, dashboard/, map/, components/
 └─ MainActivity.kt
```

### Data Files
- `app/src/main/assets/barangays.json`: All 42 official barangays of Nasugbu (12 Poblacion, 30 Rural & Coastal). Aliases like "Balaytigue" for Balaytigui and "Munting Indang" for Munting Indan are supported. Balok-Balok is intentionally omitted as it is not an official barangay.
- `app/src/main/assets/branches.json`: 23 retail branches decoded from Google Maps Plus Codes (8 DALI, 3 7-Eleven, 12 Alfamart).

### Default Service Radii
- **DALI Everyday Grocery:** 1.0 km
- **7-Eleven:** 0.6 km
- **Alfamart:** 0.5 km

---

## 4. How to Add New Branches

To register a new store branch:

1. Open `app/src/main/assets/branches.json`.
2. Append a new JSON object entry:
   ```json
   {
     "id": "DALI-009",
     "brand": "DALI",
     "branchName": "DALI New Branch",
     "latitude": 14.076500,
     "longitude": 120.631200,
     "serviceRadiusKm": 1.0,
     "plusCode": "3JHJ+XX",
     "addressNote": "Sample Address, Barangay 4"
   }
   ```
   - Supported `brand` values: `"DALI"`, `"SEVEN_ELEVEN"`, `"ALFAMART"`.
   - If `serviceRadiusKm` is omitted, the app automatically applies the default radius for that brand.

---

## 5. Building and Testing

Run unit tests via Gradle:
```bash
./gradlew test
```
- Tests Haversine distance calculations against known points (e.g. Barangay 4 to Barangay 11 ~1.46 km / ~1.5 km).
- Tests coverage use case logic (points inside, exactly on boundary, outside, and nearest-first ordering).
- Tests repository integrity (verifying exactly 42 barangays with unique IDs, alias resolution, group distribution, and absence of Balok-Balok).
- Tests branch repository integrity (23 branches, unique IDs, correct default radii).

Build the debug APK:
```bash
./gradlew assembleDebug
```
