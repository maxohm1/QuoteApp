# QuoteVault 📱✨

Hey there! Welcome to **QuoteVault** - a beautiful quote app I built using Kotlin and Jetpack Compose. It's like having a pocket full of inspiration that you can carry with you everywhere!

## What Does This App Do?

QuoteVault helps you discover, save, and share inspiring quotes. Here's what you can do with it:

### 🔐 User Accounts
- Sign up with your email and password
- Login and stay logged in (no need to login every time!)
- Forgot your password? No worries, you can reset it
- Customize your profile with your name

### 📖 Browse Quotes
- See a beautiful feed of quotes on the home screen
- Browse quotes by category (Motivation, Love, Success, Wisdom, Humor, and more!)
- Search for quotes by keyword or author name
- Pull down to refresh and get new quotes

### ❤️ Save Your Favorites
- Tap the heart button to save quotes you love
- See all your favorite quotes in one place
- Create your own collections (like "Morning Motivation" or "Work Inspiration")
- Your favorites sync across devices when you're logged in

### 🔔 Daily Inspiration
- Get a fresh "Quote of the Day" every morning
- Set a notification to remind you at your preferred time
- Never miss your daily dose of inspiration!

### 📤 Share With Friends
- Share quotes as text to WhatsApp, Instagram, or anywhere
- Create beautiful quote cards with styled backgrounds
- Save quote images to your phone
- Choose from 6 different card designs

### 🎨 Make It Yours
- Switch between Dark and Light mode
- Pick your favorite accent color (Purple, Blue, Teal, Orange, or Pink)
- Adjust the font size to your liking
- Add a widget to your home screen

---

## 🚀 How to Set Up This Project

### What You'll Need
- **Android Studio** (Hedgehog version or newer)
- **JDK 17** (Java Development Kit)
- **Android SDK 35**

### Step 1: Get the Code

```bash
git clone https://github.com/maxohm1/QuoteApp.git
```

Open this folder in Android Studio.

### Step 2: Set Up Supabase (The Backend)

This app uses **Supabase** as its backend for storing quotes and user data. Here's how to set it up:

1. **Create a Supabase Account**
   - Go to [supabase.com](https://supabase.com) and sign up (it's free!)
   - Create a new project and give it a name

2. **Create the Database Tables**
   - Go to the SQL Editor in your Supabase dashboard
   - Copy and paste this SQL code and run it:

```sql
-- This creates the quotes table
CREATE TABLE quotes (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    text TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- This stores user favorites
CREATE TABLE favorites (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    quote_id UUID REFERENCES quotes(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, quote_id)
);

-- This stores user collections
CREATE TABLE collections (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT DEFAULT '',
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- This links quotes to collections
CREATE TABLE collection_quotes (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    collection_id UUID REFERENCES collections(id) ON DELETE CASCADE,
    quote_id UUID REFERENCES quotes(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(collection_id, quote_id)
);

-- This stores user profile info
CREATE TABLE user_profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    email TEXT NOT NULL,
    display_name TEXT NOT NULL,
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

3. **Turn on Row Level Security**
   - Go to each table and enable RLS (Row Level Security)
   - This keeps user data safe and private

4. **Add Your Supabase Keys to the App**
   - Find your project URL and anon key in Supabase (Settings → API)
   - Open `local.properties` in the project root
   - Add these lines:

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
```

5. **Add Some Quotes to the Database**
   - There's a file called `seed_quotes.sql` in the `supabase` folder
   - Run it in the Supabase SQL Editor to add 100+ quotes

### Step 3: Run the App

- Click the green "Run" button in Android Studio
- Choose your phone or emulator
- That's it! The app should start running 🎉

---

## 🤖 How I Built This with AI

I used AI tools to help me build this app faster and write better code. Here's my approach:

### AI Tools I Used

| Tool | What I Used It For |
|------|-------------------|
| **Google Gemini (Antigravity)** | Main coding assistant - helped with architecture, debugging, and features |
| **GitHub Copilot** | Code completion and quick suggestions while typing |

### My AI Workflow

1. **Planning First** - I described what I wanted to build and asked AI to help design the app structure
2. **Write Code Together** - AI helped me write the tricky parts like authentication, database queries, and animations
3. **Fix Bugs Quickly** - When something broke, AI helped me figure out why and fix it fast
4. **Polish the UI** - AI suggested animations and styling to make the app feel premium
5. **Write Documentation** - This README was written with AI help too!

### What Worked Well
- AI was great at boilerplate code (stuff that's repetitive)
- It understood Kotlin and Jetpack Compose really well
- Debugging was much faster with AI explaining error messages
- UI animations came out smooth with AI suggestions

---

## 🎨 Design

The app has a clean, modern look with:
- Smooth animations when you tap cards
- Staggered list animations (cards slide in one after another)
- Transparent bottom navigation bar
- Dark and Light themes that actually look good
- Beautiful gradient quote cards

**Design Tools Used:**
- The UI was inspired by modern Material Design 3 guidelines
- Animations were implemented using Jetpack Compose animation APIs

*(No Figma/Stitch designs were used - the UI was built iteratively with code)*

---

## 🛠️ Tech Stack

Here's what's under the hood:

| What | Technology |
|------|------------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt |
| **Local Database** | Room |
| **Backend** | Supabase (Auth + PostgreSQL) |
| **Networking** | Ktor Client |
| **Preferences** | DataStore |
| **Background Work** | WorkManager + AlarmManager |
| **Widget** | Glance |

---

## ⚠️ Known Limitations & Incomplete Features

Here's what's not perfect yet:

1. **Notifications on some phones** - On Realme, Xiaomi, and other Chinese phones, you might need to disable battery optimization for notifications to work reliably

2. **Widget refresh** - The home screen widget updates once a day, not in real-time

3. **Time picker format** - The notification time picker uses 24-hour format only

4. **Avatar upload** - Profile picture upload isn't implemented yet (shows default avatar)

5. **Offline mode** - The app needs internet to fetch quotes (local caching is basic)

6. **Quote of the Day** - Sometimes takes a moment to load on first open

---

## 📁 Project Structure

```
app/src/main/java/max/ohm/quoteapp/
├── data/           → Database and API stuff
├── di/             → Dependency injection setup
├── domain/         → Business logic and models
├── presentation/   → All the screens and UI
├── ui/theme/       → Colors, fonts, and theming
├── util/           → Helper functions
├── widget/         → Home screen widget
├── worker/         → Background jobs
└── receiver/       → Broadcast receivers (alarms)
```

---

## 📄 License

This project was built for learning and assessment purposes.

---

Made with ❤️ and a lot of AI help!
