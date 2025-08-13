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
        DOCKER_COMPOSE_PATH = "/secure-submodule/docker/docker-compose.yml"
        USER_API_DOCKERFILE_PATH = "/secure-submodule/docker/user-api.Dockerfile"
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
                sh "docker image prune -a -f"
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
                    //  무중단 설정 전 GREEN_CONTAINER를 8080으로 띄우고 있었음. 그에 맞게 매핑
                    def containerPortMap = [
                        (GREEN_CONTAINER): 8080,
                        (BLUE_CONTAINER) : 8081
                    ]
                    // 현재 실행 중인 컨테이너 확인 
                    def active = sh(
                        script: "docker ps --filter 'name=${GREEN_CONTAINER}' --filter 'name=${BLUE_CONTAINER}' --format '{{.Names}}'",
                        returnStdout: true
                    ).trim()

                     // 현재가 없으면 BLUE → 첫 배포
                    def next = (!active || active == BLUE_CONTAINER) ? GREEN_CONTAINER : BLUE_CONTAINER

                    // 새 컨테이너 포트 결정
                    def active_port = active ? containerPortMap[active] : null
                    def next_port = containerPortMap[next]

                    echo "▶️ Active container: ${active ?: 'None'} ${active_port ? "(port ${active_port})" : ''}"
                    echo "🔄 Next container: ${next} (port ${next_port})"

                    // 새 컨테이너 시작
                    sh "docker run -d --name ${next} -p ${next_port}:8080 ${IMAGE_NAME}:latest"

                    // 컨테이너 정상 구동 체크
                    sh """
                        for i in {1..10}; do
                            curl -fs http://localhost:${next_port}/health && break
                            echo 'Waiting for container...'
                            sleep 3
                        done || { echo '❌ Container failed to start'; exit 1; }
                    """

                    // Nginx upstream 갱신 및 reload
                    sh """
                        echo 'upstream user_api_upstream { server localhost:${next_port}; }' > ${NGINX_UPSTREAM_CONF}
                        nginx -s reload
                    """

                    if(active) {
                        echo "🛑 Stopping old container: ${active}"
                        sh "docker rm -f ${active} || true"
                    }

                    echo "✅ Traffic switched to ${next} (port ${next_port}) via Nginx"
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
