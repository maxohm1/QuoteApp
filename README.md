# QuoteVault

Hey there! Welcome to **QuoteVault** - a beautiful quote app I built using Kotlin and Jetpack Compose. It's like having a pocket full of inspiration that you can carry with you everywhere!

## What Does This App Do?

QuoteVault helps you discover, save, and share inspiring quotes. Here's what you can do with it:

###  User Accounts
- Sign up with your email and password
- Login and stay logged in (no need to login every time!)
- Forgot your password? No worries, you can reset it
- Customize your profile with your name

###  Browse Quotes
- See a beautiful feed of quotes on the home screen
- Browse quotes by category (Motivation, Love, Success, Wisdom, Humor, and more!)
- Search for quotes by keyword or author name
- Pull down to refresh and get new quotes

###  Save Your Favorites
- Tap the heart button to save quotes you love
- See all your favorite quotes in one place
- Create your own collections (like "Morning Motivation" or "Work Inspiration")
- Your favorites sync across devices when you're logged in

###  Daily Inspiration
- Get a fresh "Quote of the Day" every morning
- Set a notification to remind you at your preferred time
- Never miss your daily dose of inspiration!

###  Share With Friends
- Share quotes as text to WhatsApp, Instagram, or anywhere
- Create beautiful quote cards with styled backgrounds
- Save quote images to your phone
- Choose from 6 different card designs

###  Make It Yours
- Switch between Dark and Light mode
- Pick your favorite accent color (Purple, Blue, Teal, Orange, or Pink)
- Adjust the font size to your liking
- Add a widget to your home screen

---

##  How to Set Up This Project

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
- Hit the play button

---

##  How I Built This with AI

I used AI tools to help me build this app faster and write better code. Here's my approach:

### AI Tools I Used

| Tool | What I Used It For |
|------|-------------------|
| **Google Antigravity(Gemini 3 pro and Claude opus 4.5 thinking)** | Main coding assistant - helped with architecture, debugging, and features |
| **GitHub Copilot** | Code completion and quick suggestions while typing |

### My AI Workflow

1. **Planning First** - I described what I wanted to build and asked AI to help design the app structure
2. **Write Code Together** - AI helped me write the tricky parts like authentication, database queries, and animations
3. **Fix Bugs Quickly** - When something broke, AI helped me figure out why and fix it fast
4. **Polish the UI** - AI suggested animations and styling to make the app feel premium

### What Worked Well
- AI was great at boilerplate code (stuff that's repetitive)
- It understood Kotlin and Jetpack Compose really well
- Debugging was much faster with AI explaining error messages
- UI animations came out smooth with AI suggestions

---

##  Design

The app has a clean, modern look with:
- Smooth animations when you tap cards
- Staggered list animations (cards slide in one after another)
- Transparent bottom navigation bar
- Dark and Light themes that actually look good
- Beautiful gradient quote cards

**Design Tools Used:**
- The UI was designed using Google Stitch by writing modern, professional UI prompts for each screen and refining them through 2–3 iterations also have screenshot.
- For each screen, I copied the generated HTML layout from Stitch and used Antigravity AI to convert it into Kotlin (Jetpack Compose) code.
- Minor light and dark mode inconsistencies from the generated designs were manually corrected during implementation.
- The final UI follows Material Design 3 guidelines and uses Jetpack Compose animation APIs for smooth interactions.
- here is the link of Stitch https://stitch.withgoogle.com/projects/9863324927037199544

---

##  Tech Stack

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

##  Known Limitations & Incomplete Features

Here's what's not perfect yet:

1. **Widget refresh** - The home screen widget updates once a day, not in real-time

2. **Time picker format** - The notification time picker uses 24-hour format only

---



## Sturcture and Supabase
![WhatsApp Image 2026-01-15 at 11 03 51 AM](https://github.com/user-attachments/assets/0ddae80b-51de-4dd2-8f32-3f3d86cc33b7)
<img width="1000" height="700" alt="Screenshot (1007)" src="https://github.com/user-attachments/assets/034f3ba9-5ff2-40fa-a5da-b7a2040eee74" />
<img width="1920" height="1080" alt="Screenshot (1003)" src="https://github.com/user-attachments/assets/157fce21-a8bc-42aa-bbeb-dac01f51e025" />
<img width="1920" height="1080" alt="Screenshot (1008)" src="https://github.com/user-attachments/assets/4018c7b9-47d9-4fa9-8f46-83a8f841650d" />

 
---




<div align="center">

**This Assignment takes my time and effort please consider it.
Check Out <img src="https://images.emojiterra.com/google/noto-emoji/animated-emoji/2764.gif" height="30" alt="love" /> More <img src="https://github.com/maxohm1/OneAI-ScreenShot/blob/main/200w.gif" height="40" />**

<br><br>

<a href="https://www.linkedin.com/in/om-prakash-mandal-a253a12a6/" target="_blank">
    <img src="https://github.com/maxohm1/OneAI-ScreenShot/blob/main/372102050_LINKEDIN_ICON_TRANSPARENT_1080.gif" width="150" />
</a>

<a href="https://play.google.com/store/apps/details?id=max.ohm.oneai&hl=en" target="_blank">
    <img src="https://user-images.githubusercontent.com/74038190/212281763-e6ecd7ef-c4aa-45b6-a97c-f33f6bb592bd.gif" width="100" />
</a>

<a href="https://omportfolio-liard.vercel.app" target="_blank">
    <img src="https://cdn.dribbble.com/userupload/28117148/file/original-c0db2041822a946b9529b5ae1fdf08e8.gif" width="140" />
</a>

---


