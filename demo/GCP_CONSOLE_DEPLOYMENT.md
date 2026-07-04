# Deploy to GCP Without CLI - Using Google Cloud Console

## Prerequisites
- Google Cloud Account (free tier available)
- Your GitHub repository (or upload code manually)
- Docker image ready

## Option 1: Deploy via Cloud Console UI (Easiest)

### Step 1: Create a GCP Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on the project dropdown at the top
3. Click "NEW PROJECT"
4. Enter project name: "employee-app"
5. Click "CREATE"

### Step 2: Enable Cloud Run API
1. In the console search bar, type "Cloud Run"
2. Click on "Cloud Run" service
3. Click "ENABLE" if prompted
4. Wait for the API to enable

### Step 3: Deploy to Cloud Run
1. In Cloud Run console, click "CREATE SERVICE"
2. Choose deployment option:
   - **Option A: Deploy from Container Image**
   - **Option B: Deploy from Source Code**

### **Method A: From Container Image (Using Artifact Registry)**

#### Step A1: Upload Docker Image to Artifact Registry
1. Go to **Artifact Registry** in console search
2. Click **CREATE REPOSITORY**
3. Fill in:
   - Name: `employee-app`
   - Format: `Docker`
   - Location: `us-central1`
4. Click **CREATE**

#### Step A2: Upload Image
1. Open **Cloud Shell** (terminal icon in top right)
2. Clone your repository:
```bash
git clone https://github.com/YOUR-USERNAME/employee-app.git
cd employee-app
```

3. Build and push image:
```bash
# Set variables
export PROJECT_ID=$(gcloud config get-value project)
export REGION=us-central1

# Build image
gcloud builds submit --region=$REGION \
  --tag=$REGION-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:latest
```

#### Step A3: Deploy from Image
1. Go back to **Cloud Run**
2. Click **CREATE SERVICE**
3. Select **Deploy one revision from an existing container image**
4. Click **SELECT**
5. Choose your image from Artifact Registry
6. Fill in service details:
   - Service name: `employee-app`
   - Region: `us-central1`
   - CPU: `1`
   - Memory: `512 MB`
   - Timeout: `3600`
   - Allow public access: **YES** (check "Allow unauthenticated invocations")
7. Click **CREATE**

---

### **Method B: Deploy from Source Code (Easiest!)**

#### Step B1: Push Code to GitHub/GitLab
1. Push your code to GitHub if not already there
2. Make sure your repository includes:
   - `Dockerfile`
   - `pom.xml`
   - `src/` folder

#### Step B2: Deploy via Cloud Console
1. Go to **Cloud Run** in console
2. Click **CREATE SERVICE**
3. Select **Continuously deploy from a repository**
4. Click **SET UP WITH CLOUD BUILD**
5. Choose your code source:
   - GitHub
   - GitLab
   - Bitbucket
   - Google Cloud Source Repositories
6. Authenticate and select your repository
7. Select branch: `main` or `master`
8. Configure build settings:
   - Build type: `Dockerfile`
   - Dockerfile location: `/Dockerfile`
9. Fill in service details:
   - Service name: `employee-app`
   - Region: `us-central1`
   - Memory: `512 MB`
   - Allow public access: **YES**
10. Click **CREATE**

---

## Option 2: Using Cloud Build (Visual Workflow)

### Step 1: Connect Repository
1. Go to **Cloud Build** → **Repositories**
2. Click **CONNECT REPOSITORY**
3. Select your source (GitHub/GitLab)
4. Authenticate and authorize Cloud Build
5. Select your repository

### Step 2: Create Build Trigger
1. Go to **Cloud Build** → **Triggers**
2. Click **CREATE TRIGGER**
3. Fill in:
   - Name: `deploy-employee-app`
   - Repository: Select your connected repo
   - Branch: `^main$` (or your branch)
   - Build configuration: `Cloud Build configuration file (yaml)`
   - Cloud Build configuration file location: `cloudbuild.yaml`
4. Click **CREATE**

### Step 3: Auto-Deploy on Push
1. Every time you push to `main`, Cloud Build will:
   - Build the Docker image
   - Push to Artifact Registry
   - Deploy to Cloud Run automatically

---

## Option 3: Manual Upload (No Git)

### Step 1: Upload Code to Cloud Storage
1. Create a **Cloud Storage bucket**:
   - Name: `employee-app-source-[UNIQUE-ID]`
   - Region: `us-central1`
2. Upload your code as a ZIP file

### Step 2: Build with Cloud Build
1. Go to **Cloud Build** → **Dashboard**
2. Click **RUN BUILD**
3. Select **Cloud Storage** as source
4. Upload your code ZIP
5. Configure build steps and deploy

---

## Testing Your Deployed App

### Via Cloud Console:
1. Go to **Cloud Run** → Select your service
2. Copy the **Service URL** (e.g., `https://employee-app-xxxxxx.run.app`)
3. Test with curl or Postman:

```bash
curl -X POST https://employee-app-xxxxxx.run.app/employee/getId \
  -H "Content-Type: application/json" \
  -d '{"employeeName":"Leon"}'
```

### Expected Response:
```json
{
  "employeeId": 1,
  "employeeName": "Leon"
}
```

---

## Monitoring & Logs

1. Go to **Cloud Run** → Select your service
2. View:
   - **Metrics**: Requests, latency, errors
   - **Logs**: Real-time application logs
   - **Revisions**: Version history

---

## Cost Estimate (Free Tier Eligible)

- **Cloud Run**: 2 million requests/month FREE
- **Artifact Registry**: 0.5GB storage FREE
- **Cloud Build**: 120 minutes/day FREE

Your first deployment will likely be **completely free**!

---

## Troubleshooting

### If deployment fails:
1. Check **Cloud Build** logs for build errors
2. Verify Dockerfile is correct
3. Ensure pom.xml has all dependencies
4. Check application.properties for port 8080

### Common Issues:
- **Port not 8080**: Update Dockerfile
- **Build timeout**: Increase Cloud Build timeout in settings
- **Out of memory**: Increase Cloud Run memory to 1 GB
