#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 LLM Insurance Backend - Cloud Run 배포 스크립트${NC}"
echo "=================================================="

# 1. 환경 변수 설정
PROJECT_ID="${GCP_PROJECT_ID:-concise-dolphin-465907-s7}"
REGION="${GCP_REGION:-us-central1}"
SERVICE_NAME="llminsurance-backend"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Cloud SQL 설정
CLOUDSQL_INSTANCE="${PROJECT_ID}:${REGION}:llminsurance-postgres"
# DB_PASSWORD는 반드시 환경변수로 설정해야 함
if [ -z "$DB_PASSWORD" ]; then
    echo -e "${RED}❌ DB_PASSWORD 환경변수가 설정되지 않았습니다.${NC}"
    echo "다음 명령어로 설정하세요: export DB_PASSWORD=your_secure_password"
    exit 1
fi

echo -e "${YELLOW}📋 배포 설정:${NC}"
echo "  프로젝트 ID: $PROJECT_ID"
echo "  리전: $REGION"
echo "  서비스명: $SERVICE_NAME"
echo "  이미지: $IMAGE_NAME"
echo ""

# 2. GCP 인증 확인
echo -e "${YELLOW}🔐 GCP 인증 확인 중...${NC}"
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q "@"; then
    echo -e "${RED}❌ GCP 인증이 필요합니다. 다음 명령어를 실행하세요:${NC}"
    echo "gcloud auth login"
    exit 1
fi

# 3. 프로젝트 설정
echo -e "${YELLOW}⚙️  프로젝트 설정 중...${NC}"
gcloud config set project $PROJECT_ID

# 4. 필요한 API 활성화
echo -e "${YELLOW}🔧 필요한 API 활성화 중...${NC}"
gcloud services enable cloudbuild.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable containerregistry.googleapis.com

# 5. Docker 이미지 빌드 (amd64 플랫폼 - 애플 실리콘)
echo -e "${YELLOW}🐳 Docker 이미지 빌드 중 (amd64 플랫폼)...${NC}"
docker buildx build --platform linux/amd64 -t $IMAGE_NAME:latest --push .

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Docker 이미지 빌드 실패${NC}"
    exit 1
fi

# 6. 이미지 푸시는 buildx --push 옵션으로 자동 처리됨
echo -e "${GREEN}✅ 이미지 빌드 및 푸시 완료${NC}"

# 7. Cloud Run에 배포
echo -e "${YELLOW}🚀 Cloud Run에 배포 중...${NC}"
gcloud run deploy $SERVICE_NAME \
    --image $IMAGE_NAME:latest \
    --region $REGION \
    --platform managed \
    --allow-unauthenticated \
    --port 8080 \
    --set-env-vars "SPRING_PROFILES_ACTIVE=dev" \
    --memory 1Gi \
    --cpu 1 \
    --max-instances 10 \
    --timeout 300

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Cloud Run 배포 실패${NC}"
    exit 1
fi

# 8. 서비스 URL 가져오기
echo -e "${YELLOW}🔍 서비스 URL 확인 중...${NC}"
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format='value(status.url)')

echo -e "${GREEN}✅ 배포 완료!${NC}"
echo ""
echo -e "${GREEN}🌐 서비스 URL: $SERVICE_URL${NC}"
echo -e "${GREEN}🔧 Health Check: $SERVICE_URL/actuator/health${NC}"
echo ""
echo -e "${BLUE}📝 다음 단계:${NC}"
echo "1. Cloud SQL 설정: ./setup-cloudsql.sh"
echo "2. 환경 변수 설정 (데이터베이스, JWT, OpenAI API 키)"
echo "3. API 테스트"
echo "" 