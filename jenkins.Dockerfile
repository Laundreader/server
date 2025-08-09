FROM jenkins/jenkins:lts

USER root

# 기본 패키지 업데이트 및 필수 도구 설치
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    software-properties-common

# -----------------------------
# 1. JDK 21 설치 (Temurin)
# -----------------------------
RUN mkdir -p /usr/share/man/man1 && \
    curl -fsSL https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor -o /usr/share/keyrings/adoptium.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" \
    | tee /etc/apt/sources.list.d/adoptium.list && \
    apt-get update && apt-get install -y temurin-21-jdk

ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk
ENV PATH=$JAVA_HOME/bin:$PATH

# -----------------------------
# 2. Gradle 8.4 설치
# -----------------------------
ENV GRADLE_VERSION=8.4
RUN curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip gradle.zip -d /opt && \
    rm gradle.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle

ENV GRADLE_HOME=/opt/gradle
ENV PATH=$GRADLE_HOME/bin:$PATH

# -----------------------------
# 3. Docker CLI 설치
# -----------------------------
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker.gpg] https://download.docker.com/linux/debian \
    $(lsb_release -cs) stable" \
    | tee /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli docker-ce

# jenkins 유저가 docker 명령 가능하도록 설정
RUN groupadd -for docker && usermod -aG docker jenkins

# -----------------------------
# 4. Jenkins 기본 포트 변경
# -----------------------------
ENV JENKINS_OPTS="--httpPort=9000"

USER jenkins
