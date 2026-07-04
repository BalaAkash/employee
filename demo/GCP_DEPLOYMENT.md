# Deployment Guide for GCP

## Prerequisites
- Google Cloud Account with a project
- `gcloud` CLI installed
- Docker installed locally (for testing)

## Local Testing (Optional)

Build and test Docker image locally:

```bash
# Build image
docker build -t employee-app:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  employee-app:latest

# Test endpoint
curl -X POST http://localhost:8080/employee/getId \
  -H "Content-Type: application/json" \
  -d '{"employeeName":"Leon"}'
```

## Deploy to GCP Cloud Run

### 1. Set up GCP Project
```bash
# Set project ID
export PROJECT_ID=your-project-id
gcloud config set project $PROJECT_ID

# Enable required APIs
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

### 2. Configure Artifact Registry (Container Repository)
```bash
export REGION=us-central1
export REPO=employee-app

# Create Artifact Registry repository
gcloud artifacts repositories create $REPO \
  --repository-format=docker \
  --location=$REGION

# Configure Docker authentication
gcloud auth configure-docker ${REGION}-docker.pkg.dev
```

### 3. Build and Push Image to Artifact Registry
```bash
export IMAGE_NAME=${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/employee-app:latest

# Build image using Google Cloud Build
gcloud builds submit \
  --region=$REGION \
  --tag=$IMAGE_NAME

# Or build locally and push
docker build -t $IMAGE_NAME .
docker push $IMAGE_NAME
```

### 4. Deploy to Cloud Run
```bash
export SERVICE_NAME=employee-app

gcloud run deploy $SERVICE_NAME \
  --image=$IMAGE_NAME \
  --platform=managed \
  --region=$REGION \
  --port=8080 \
  --memory=512Mi \
  --cpu=1 \
  --timeout=3600 \
  --allow-unauthenticated
```

### 5. Test Deployed Service
```bash
# Get service URL
export SERVICE_URL=$(gcloud run services describe $SERVICE_NAME \
  --region=$REGION \
  --format='value(status.url)')

# Test endpoint
curl -X POST ${SERVICE_URL}/employee/getId \
  -H "Content-Type: application/json" \
  -d '{"employeeName":"Leon"}'
```

## Alternative: Deploy using gcloud CLI Directly

```bash
gcloud run deploy employee-app \
  --source . \
  --region=us-central1 \
  --platform=managed \
  --port=8080 \
  --memory=512Mi \
  --allow-unauthenticated
```

## Using cloudbuild.yaml (CI/CD)

Create a `cloudbuild.yaml` file for automated builds:

```yaml
steps:
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 
           'us-central1-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:$COMMIT_SHA',
           '.']
  
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push',
           'us-central1-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:$COMMIT_SHA']
  
  - name: 'gcr.io/cloud-builders/gke-deploy'
    args:
      - run
      - --filename=.
      - --image=us-central1-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:$COMMIT_SHA
      - --location=us-central1
      - --cluster=my-cluster

images:
  - 'us-central1-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:$COMMIT_SHA'
```

## Environment Variables (Optional)

For Cloud Run environment configuration, add these at deployment:

```bash
gcloud run deploy employee-app \
  --image=$IMAGE_NAME \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --region=us-central1 \
  --platform=managed
```

## Monitoring and Logs

```bash
# View logs
gcloud run services describe employee-app --region=us-central1

# Stream logs
gcloud run services logs read employee-app --region=us-central1 --limit=50

# Get metrics
gcloud monitoring time-series list --filter='metric.type="run.googleapis.com/request_count"'
```

## Cost Optimization Tips

- Use `--memory=256Mi` for light workloads
- Set `--max-instances=10` to limit costs
- Enable IAM authentication if API is internal
- Use Cloud Scheduler for scheduled tasks

## Cleanup

```bash
# Delete Cloud Run service
gcloud run services delete employee-app --region=us-central1

# Delete image from Artifact Registry
gcloud artifacts docker images delete \
  us-central1-docker.pkg.dev/$PROJECT_ID/employee-app/employee-app:latest
```
