#!/bin/bash

# GCP 프로젝트 설정 (실제 값으로 변경 필요)
PROJECT_ID="${GCP_PROJECT_ID:-your-gcp-project-id}"
REGION="${GCP_REGION:-asia-northeast3}"  # 서울 리전
SERVICE_NAME="${GCP_SERVICE_NAME:-llminsurance-backend}"
IMAGE_NAME="gcr.io/$PROJECT_ID/$SERVICE_NAME"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== LLM Insurance Backend GCP 배포 스크립트 ===${NC}"
echo ""

# 1. GCP 프로젝트 설정 확인
echo -e "${YELLOW}1. GCP 프로젝트 설정 중...${NC}"
gcloud config set project $PROJECT_ID
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: GCP 프로젝트 설정 실패${NC}"
    exit 1
fi

# 2. 필요한 API 활성화
echo -e "${YELLOW}2. 필요한 GCP API 활성화 중...${NC}"
gcloud services enable cloudbuild.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable containerregistry.googleapis.com

# 3. Docker 이미지 빌드
echo -e "${YELLOW}3. Docker 이미지 빌드 중...${NC}"
docker build -t $IMAGE_NAME .
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Docker 이미지 빌드 실패${NC}"
    exit 1
fi

# 4. Docker 이미지를 GCR에 푸시
echo -e "${YELLOW}4. Docker 이미지를 Container Registry에 푸시 중...${NC}"
docker push $IMAGE_NAME
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Docker 이미지 푸시 실패${NC}"
    exit 1
fi

# 5. Cloud Run에 배포
echo -e "${YELLOW}5. Cloud Run에 배포 중...${NC}"
gcloud run deploy $SERVICE_NAME \
    --image $IMAGE_NAME \
    --platform managed \
    --region $REGION \
    --allow-unauthenticated \
    --port 8080 \
    --memory 1Gi \
    --cpu 1 \
    --max-instances 10 \
    --min-instances 0 \
    --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars "PORT=8080"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 배포 완료!${NC}"
    echo ""
    echo -e "${GREEN}서비스 URL:${NC}"
    gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(status.url)'
else
    echo -e "${RED}Error: Cloud Run 배포 실패${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}참고사항:${NC}"
echo "- 환경 변수는 GCP Console 또는 gcloud 명령어로 별도 설정 필요"
echo "- 데이터베이스는 Cloud SQL 또는 외부 데이터베이스 사용"
echo "- HTTPS 인증서는 자동으로 제공됨" 