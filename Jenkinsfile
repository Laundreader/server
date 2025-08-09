pipeline {
    agent any
    tools {
        gradle 'gradle-8.4'
        jdk 'jdk-21'
    }
    environment {
        DOCKER_BUILDKIT = "0"
        IMAGE_NAME = 'user-api'
        IMAGE_TAG = "v${BUILD_NUMBER}"
        BLUE_CONTAINER = "user-api-blue"
        GREEN_CONTAINER = "user-api-green"
        WORKSPACE = "/var/jenkins_home/workspace/laundreader-prod"
        DOCKER_COMPOSE_PATH = "/secure-submodule/docker/docker-compose.yml"
        USER_API_DOCKERFILE_PATH = "/secure-submodule/docker/user-api.Dockerfile"
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
                sh './gradlew :user-api:buildNeeded --stacktrace --info -x test'
            }
        }
        stage('Docker Build'){
            steps {
                sh """
                    docker build \
                        -f ${WORKSPACE}${USER_API_DOCKERFILE_PATH} \
                        -t ${IMAGE_NAME}:${IMAGE_TAG} \
                        -t ${IMAGE_NAME}:latest \
                        .
                """
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // 현재 실행 중인 컨테이너 확인 
                    def active = sh(script: "docker ps --filter 'name=${BLUE_CONTAINER}' --format '{{.Names}}'", returnStdout: true).trim()
                    def next = active ? GREEN_CONTAINER : BLUE_CONTAINER

                    echo "▶️ Active container: ${active}"
                    echo "🔄 Next container: ${next}"

                    sh "docker rm -f ${active} || true"
                    sh "docker rm -f ${next} || true"

                     // 새 컨테이너 시작
                    sh "docker run -d --name ${next} -p 8080:8080 ${IMAGE_NAME}:latest"

                    sh "docker system prune -a -f"

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
