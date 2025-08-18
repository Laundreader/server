pipeline {
    agent any
    tools {
        gradle 'gradle-8.4'
        jdk 'jdk-21'
    }
    environment {
        WORKSPACE = "/var/jenkins_home/workspace/laundreader-prod"
        IMAGE_NAME = 'user-api'
        IMAGE_TAG = "v${BUILD_NUMBER}"
        BLUE_COMPOSE = "${WORKSPACE}/secure-submodule/docker/docker-compose.blue.yml"
        GREEN_COMPOSE = "${WORKSPACE}/secure-submodule/docker/docker-compose.green.yml"
        USER_API_DOCKERFILE_PATH = "/secure-submodule/docker/user-api.Dockerfile"
        HOST_IP = "49.50.133.246"
        NGINX_UPSTREAM_CONF = "/etc/nginx/conf.d/user-api-upstream.conf"
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
        stage('Docker Build'){
            steps {
                sh """
                    DOCKER_BUILDKIT=0 docker build \
                        -f ${WORKSPACE}${USER_API_DOCKERFILE_PATH} \
                        -t ${IMAGE_NAME}:${IMAGE_TAG} \
                        -t ${IMAGE_NAME}:latest \
                        ${WORKSPACE}/user-api/build/libs
                """
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // 현재 활성 컨테이너 확인 (Blue 또는 Green)
                    def active = sh(
                        script: "docker ps --filter 'name=user-api-blue' --filter 'name=user-api-green' --format '{{.Names}}' | head -n 1",
                        returnStdout: true
                    ).trim()

                    // 다음 배포 대상 결정
                    def nextCompose
                    def nextService
                    if (!active || active == 'user-api-green') {
                        nextCompose = BLUE_COMPOSE
                        nextService = 'user-api-blue'
                    } else {
                        nextCompose = GREEN_COMPOSE
                        nextService = 'user-api-green'
                    }

                    echo "▶️ Active container: ${active ?: 'None'}"
                    echo "🔄 Next deploy: ${nextService} using ${nextCompose}"

                    // 새 컨테이너 시작
                    sh "docker-compose -f ${nextCompose} up -d --build"

                    // 새 컨테이너 정상 구동 확인
                    def nextPort = nextService == 'user-api-blue' ? 8080 : 8081
                    sh """
                        for i in {1..5}; do
                            curl -fs http://localhost:${nextPort}/health && break
                            echo 'Waiting for container...'
                            sleep 3
                        done || { echo '❌ Container failed to start'; exit 1; }
                    """

                    // Nginx upstream 갱신
                    sshagent (credentials: ['jenkins-ssh-key']) {
                        sh """
                        ssh -o StrictHostKeyChecking=no root@${HOST_IP} \\
                            "echo 'upstream user_api_upstream { server localhost:${nextPort}; }' > ${NGINX_UPSTREAM_CONF} &&
                             nginx -t &&
                             systemctl reload nginx"
                        """
                    }

                    // 이전 컨테이너 종료
                    if(active) {
                        echo "Stopping old container: ${active}"
                        sh "docker rm -f ${active} || true"
                    }

                    echo "✅ Traffic switched to ${nextService} (port ${nextPort}) via Nginx"
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
