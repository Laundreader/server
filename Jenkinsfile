pipeline {
    agent any
    tools {
        gradle 'gradle-8.4'
        jdk 'jdk-21'
    }
    environment {
        IMAGE_NAME = 'user-api'
        IMAGE_TAG = "v${BUILD_NUMBER}"
        BLUE_CONTAINER = "user-api-blue"
        GREEN_CONTAINER = "user-api-green"
        WORKSPACE = "/var/jenkins_home/workspace/laundreader-prod"
        DOCKER_COMPOSE_PATH = "secure-submodule/docker/docker-compose.yml"
        USER_API_DOCKERFILE_PATH = "secure-submodule/docker/user-api.Dockerfile"
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
        stage('Prepare env') {
            steps {
                // env 파일 복사
                sh "cp ${WORKSPACE}/secure-submodule/env/env.yml ${WORKSPACE}/user-api/src/main/resources/env.yml"
                sh "cp ${WORKSPACE}/secure-submodule/env/env.yml ${WORKSPACE}/external/src/main/resources/env.yml"
            }

        }
        stage('Build JAR') {
            steps {
                sh "chmod +x ./gradlew"
                sh './gradlew clean :user-api:buildNeeded --stacktrace --info -x test' 
            }
        }
        stage('Docker Build') {
            steps {
                sh """
                        docker build -f ${WORKSPACE}/${USER_API_DOCKERFILE_PATH} -t ${IMAGE_NAME}:${IMAGE_TAG} .
                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                    """
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // blue/green 배포 시, 기존 컨테이너와 새 컨테이너를 번갈아 띄우고, health check 후 트래픽을 전환합니다.
                    // Redis는 항상 단일 인스턴스(server_redis)로 유지되어, user-api 컨테이너가 교체되어도 세션/캐시 등 데이터가 유지됩니다.

                    // 현재 실행 중인 컨테이너 확인 
                    def active = sh(script: "docker ps --filter 'name=${BLUE_CONTAINER}' --format '{{.Names}}'", returnStdout: true).trim()
                    def next = active ? GREEN_CONTAINER : BLUE_CONTAINER

                    echo "▶️ Active container: ${active}"
                    echo "🔄 Next container: ${next}"

                    // 새 컨테이너 임시 포트에 띄우기 
                    sh "docker run -d --name ${next} --network server_default -p 8081:8080 ${IMAGE_NAME}:latest"

                    // 헬스 체크 (예: /health 엔드포인트)
                    sh "sleep 10"
                    def health = sh(script: "curl -s http://localhost:8081/health", returnStdout: true).trim()
                    if (health != 'OK') {
                        error("❌ Health check failed for ${next}")
                    }

                    // 트래픽 전환: Todo: nginx 로 포트 바인딩하는 방식으로 디벨롭
                    // 이전 컨테이너 제거
                    // 현재 포트 8080 컨테이너 제거
                    if (active) {
                        sh "docker rm -f ${active} || true"
                    }

                     // 새 컨테이너 재시작 (실제 포트로)
                    sh "docker rm -f ${next} || true"
                    sh "docker run -d --name ${next} --network server_default -p 8080:8080 ${IMAGE_NAME}:latest"

                    echo "✅ Traffic switched to ${next} on port 8080"
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
