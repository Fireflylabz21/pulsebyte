# TapTap — Google Play Store Submission Package

This document contains complete, production-ready store listing details, metadata, target audience, data safety disclosures, and policy templates required for submitting **TapTap** to the Google Play Store.

---

## 1. App Category
* **Primary Category**: **News & Magazines**
* **Secondary / Alternative Category**: **Finance** (due to the BusinessHub live market & indices tracker)

---

## 2. Short Description (Max 80 characters)
> AI-powered news aggregator & live financial hub for Sensex, Nifty & global markets.

---

## 3. Long Description (Max 4000 characters)
Experience the future of news consumption and financial intelligence with **TapTap**, an AI-native news aggregator designed to eliminate information overload and news bias.

### 🌟 Key Features:
* **AI-Native Summarization**: Get straight to the point with concise, AI-synthesized 4-bullet point summaries for every top story across 12 major categories (Politics, Crime, Business, Entertainment, Sports, Health, Science & Tech, Weather, Lifestyle, Cricket, Enterprise AI, and India).
* **BusinessHub & Live Market Tracker**: Stay ahead of the markets with real-time live tickers for **Sensex (^BSESN)**, **Nifty 50 (^NSEI)**, **S&P 500 (^GSPC)**, Nasdaq, FTSE 100, and Nikkei 225.
* **Top Stock Gainers & Losers**: Track real-time top performing and lagging stocks across Day (1D) and Week (1W) timeframes.
* **Commodities & Macro Indicators**: Monitor live pricing for Brent Crude Oil, Gold, Silver, and Copper alongside key macroeconomic indicators like Repo Rates and CPI inflation.
* **Sector Insights & Correlations**: Understand how macroeconomic events impact specific sectors (Banking, IT, Auto, FMCG, Real Estate).
* **Interactive Story Flashcards**: Swipe through immersive flashcard digests designed for quick morning briefings.
* **Audio Briefings**: Listen to daily AI news briefings on the go with built-in Text-to-Speech support.
* **Bookmarks & Built-in Web Reader**: Save stories for later and read full original reporting directly inside the app.

Download TapTap today and master your daily news and market intelligence in minutes!

---

## 4. Privacy Policy Template
**Effective Date**: October 27, 2026
**Developer**: Firefly Labz (`fireflylabz@gmail.com`)

### Overview
TapTap ("we", "our", or "app") respects your privacy. This Privacy Policy explains how information is handled when you use our mobile application.

### Information Collection & Usage
* **Local Preferences**: TapTap stores your bookmarked stories and UI preferences locally on your device using encrypted/local SharedPreferences.
* **Authentication & Supabase**: If you sign in or sync data, we utilize Supabase for secure cloud storage of user bookmarks and synchronized news clusters.
* **Live Financial API**: TapTap fetches public financial market data and stock indices via standard REST APIs (such as Yahoo Finance). No personal financial data is collected or transmitted.
* **AI Processing**: News articles are synthesized using automated AI workflows. We do not collect or store personal user prompts or identities.

### Contact Us
For any privacy-related questions or requests, contact us at: **fireflylabz@gmail.com**.

---

## 5. Target Audience & Content Rating
* **Target Age Group**: **13+** (Teens and Adults)
* **Content Rating Questionnaire**:
  * Does the app contain violence, sexual content, or hate speech? **No** (Strict automated safety guardrails filter out NSFW/CSAM/harmful content).
  * Does it feature financial trading or financial information? **Yes** (Provides informational financial market indices and stock movers; no direct financial transactions or brokerage services are performed).

---

## 6. Store Tags
* News
* Finance
* Stock Market
* AI Summarizer
* Business
* Live Indices
* Daily Briefing

---

## 7. Data Safety Section Disclosures (Google Play Console)
* **Does your app collect or share any required user data types?**
  * *App info and performance / Diagnostics*: No.
  * *Personal Info (e.g., Name, Email)*: Only if user explicitly authenticates via Supabase Auth (Email optional).
  * *Financial Info*: **No** (App displays public market indices and stock movers via REST APIs; does not handle banking or financial transactions).
* **Is data encrypted in transit?** Yes (HTTPS/TLS for all Supabase and REST API calls).
* **Can users request data deletion?** Yes, by clearing app data or contacting `fireflylabz@gmail.com`.

---

## 8. Store Listing Details & Contact Info
* **App Name**: TapTap
* **Contact Email**: `fireflylabz@gmail.com`
* **Website**: `https://github.com/Fireflylabz21/pulsebyte`
