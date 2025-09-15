pipeline {
    agent any
    tools {
        gradle 'gradle-8.4'
        jdk 'jdk-21'
    }
    environment {
        IMAGE_NAME = "user-api"
        DOCKER_REPO = "chaewon012/laundreader"
        IMAGE_TAG = "v${BUILD_NUMBER}"
        USER_API_DOCKERFILE_PATH = "user-api/Dockerfile"
        COMPOSE_FILE = "docker-compose-app.yml"
        SSH_USER = "root"
        SSH_HOST = "prod-server-a.laundreader.com"
    }
    stages {
        stage('Checkout') {
            steps {
                 // 서브모듈 포함 checkout
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/develop']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Laundreader/server.git',
                        credentialsId: 'github-token'
                    ]],
                    extensions: [
                        [$class: 'SubmoduleOption', recursiveSubmodules: true]
                    ]
                ])
            }
        }
        stage('Build JAR') {
            steps {
                sh "chmod +x ./gradlew"
                sh './gradlew :user-api:buildNeeded --stacktrace --info -x test'
            }
        }
        stage('Docker Build & Push') {
            steps {
                // Docker Hub 로그인
                withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        # Docker 이미지 빌드
                        DOCKER_BUILDKIT=0 docker build \
                            -f ${USER_API_DOCKERFILE_PATH} \
                            -t ${IMAGE_NAME}:${IMAGE_TAG} user-api

                        # Docker Hub 태그 추가
                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_REPO}:${IMAGE_TAG}
                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_REPO}:latest

                        # Docker Hub로 push
                        docker push ${DOCKER_REPO}:${IMAGE_TAG}
                        docker push ${DOCKER_REPO}:latest
                    """
                }
            }
        }
        stage('Deploy') {
            steps {
                 sshagent (credentials: ['jenkins-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} '
                            # 서버 내 Git 저장소로 이동
                            cd server

                            # develop 브랜치 최신 pull
                            git fetch origin develop && git reset --hard origin/develop

                            # 최신 Docker 이미지 pull
                            docker pull ${DOCKER_REPO}:latest

                            # 기존 컨테이너 중지 및 제거 (app만)
                            docker-compose -f ${COMPOSE_FILE} down app || true

                            # 새 컨테이너 실행
                            docker-compose -f ${COMPOSE_FILE} up -d app

                            # Health 체크
                            sleep 5 &&
                            curl -fs http://localhost:8080/actuator/health  ||
                            { echo "❌ Container failed health check"; exit 1; }
                        '
                    """
                }
            }
        }
    }
    post {
        always {
            echo '파이프라인 종료'
        }
    }
}
