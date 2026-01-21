# Mapbox Authentication Setup - 100% Working Guide

## ⚡ Quick Setup (5 Minutes)

### Step 1: Get Your Mapbox Tokens

1. Visit: **https://account.mapbox.com/access-tokens/**
2. Sign up or log in
3. You need **TWO** tokens:

#### A) Public Token (Runtime)
- Find your "Default public token" or create new
- Starts with `pk.`
- Used by the app at runtime

#### B) Secret Token (Downloads) 
- Click **"Create a token"**
- Name: "KhabarLagbe Downloads"
- **CRITICAL:** Check the **"DOWNLOADS:READ"** scope ✅
- Click Create
- Copy the token (starts with `sk.`)
- ⚠️ **Save it now - you can't see it again!**

### Step 2: Configure Authentication (Choose ONE method)

#### ✅ RECOMMENDED: Global Gradle Properties (Works for All Projects)

**Windows:**
```cmd
notepad %USERPROFILE%\.gradle\gradle.properties
```

**Mac/Linux:**
```bash
nano ~/.gradle/gradle.properties
```

**Add these lines:**
```properties
MAPBOX_ACCESS_TOKEN=pk.your_public_token_here
MAPBOX_DOWNLOADS_TOKEN=sk.your_secret_token_here
```

Save and close. ✅ **Done!** This works for all Gradle projects on your machine.

#### Alternative: Project-Level (local.properties)

Create `local.properties` in project root:
```properties
MAPBOX_ACCESS_TOKEN=pk.your_public_token_here
MAPBOX_DOWNLOADS_TOKEN=sk.your_secret_token_here
```

⚠️ This file is gitignored and won't be committed.

### Step 3: Build the Project

```bash
./gradlew clean
./gradlew :app:assembleDebug
```

✅ Should build successfully!

## Troubleshooting

### ❌ "Could not find com.mapbox.navigation:android"
**Cause:** Missing or invalid MAPBOX_DOWNLOADS_TOKEN

**Fix:**
1. Verify token starts with `sk.`
2. Confirm "DOWNLOADS:READ" scope is enabled
3. Check token is in `~/.gradle/gradle.properties`
4. Run: `./gradlew --stop` then rebuild

### ❌ "401 Unauthorized"
**Cause:** Invalid secret token

**Fix:**
1. Go to https://account.mapbox.com/access-tokens/
2. Verify your token has "DOWNLOADS:READ" scope
3. Try creating a new token with the scope enabled
4. Update `~/.gradle/gradle.properties` with new token

### ❌ Token shows "pk." instead of "sk."
**Cause:** Using public token instead of secret token

**Fix:**
- `MAPBOX_ACCESS_TOKEN` = public token (pk.)
- `MAPBOX_DOWNLOADS_TOKEN` = secret token (sk.)
- Don't mix them up!

## Why This Method is Best

✅ **Secure** - Tokens stored outside project directory  
✅ **Persistent** - Works across all projects  
✅ **Git-safe** - Never accidentally committed  
✅ **Team-friendly** - Each developer uses their own tokens  
✅ **CI/CD Ready** - Easy to override with environment variables

## Verification

After setup, check the build output for:
```
✅ Mapbox authentication configured successfully
```

If you see error messages, follow the instructions displayed.

## Need Help?

See also:
- [MAPBOX_SETUP.md](./MAPBOX_SETUP.md) - Original setup guide
- [Official Mapbox Docs](https://docs.mapbox.com/android/maps/guides/install/)
