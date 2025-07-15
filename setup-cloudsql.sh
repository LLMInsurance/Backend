#!/bin/bash

# Cloud SQL 설정 (실제 값으로 변경 필요)
PROJECT_ID="${GCP_PROJECT_ID:-your-gcp-project-id}"
REGION="${GCP_REGION:-asia-northeast3}"
INSTANCE_NAME="${CLOUDSQL_INSTANCE_NAME:-llminsurance-postgres}"
DATABASE_NAME="${CLOUDSQL_DATABASE_NAME:-llminsurance}"
DB_USER="${CLOUDSQL_USER:-your_db_user}"
DB_PASSWORD="${CLOUDSQL_PASSWORD:-secure-password-here}"  # 보안을 위해 강력한 패스워드 사용

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=== Cloud SQL PostgreSQL 설정 스크립트 ===${NC}"
echo ""

# 1. Cloud SQL API 활성화
echo -e "${YELLOW}1. Cloud SQL API 활성화 중...${NC}"
gcloud services enable sqladmin.googleapis.com

# 2. PostgreSQL 인스턴스 생성
echo -e "${YELLOW}2. Cloud SQL PostgreSQL 인스턴스 생성 중...${NC}"
gcloud sql instances create $INSTANCE_NAME \
    --database-version=POSTGRES_15 \
    --tier=db-f1-micro \
    --region=$REGION \
    --storage-type=SSD \
    --storage-size=10GB \
    --backup-start-time=03:00 \
    --enable-bin-log \
    --maintenance-release-channel=production \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=4

if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Cloud SQL 인스턴스 생성 실패${NC}"
    exit 1
fi

# 3. 데이터베이스 생성
echo -e "${YELLOW}3. 데이터베이스 생성 중...${NC}"
gcloud sql databases create $DATABASE_NAME --instance=$INSTANCE_NAME

# 4. 사용자 생성 및 비밀번호 설정
echo -e "${YELLOW}4. 데이터베이스 사용자 생성 중...${NC}"
gcloud sql users create $DB_USER \
    --instance=$INSTANCE_NAME \
    --password=$DB_PASSWORD

# 5. 연결 정보 출력
echo -e "${GREEN}✅ Cloud SQL 설정 완료!${NC}"
echo ""
echo -e "${YELLOW}연결 정보:${NC}"
echo "Instance Connection Name: $(gcloud sql instances describe $INSTANCE_NAME --format='value(connectionName)')"
echo "Database Name: $DATABASE_NAME"
echo "Username: $DB_USER"
echo "Password: $DB_PASSWORD"
echo ""
echo -e "${YELLOW}Cloud Run 환경 변수 설정:${NC}"
echo "DATABASE_URL=jdbc:postgresql://google/\$DATABASE_NAME?cloudSqlInstance=\$(gcloud sql instances describe $INSTANCE_NAME --format='value(connectionName)')&socketFactory=com.google.cloud.sql.postgres.SocketFactory"
echo "DATABASE_USERNAME=$DB_USER"
echo "DATABASE_PASSWORD=$DB_PASSWORD" 