# 🫧 Laundreader

[세탁 실수 없는 생활을 위한 AI 기반 올인원 세탁도우미, 런드리더](https://laundreader.com/) <br>
> **프로젝트 기간** : 2025.8.1. ~ 2025.8.26 <br>
> **수상** : 비사이드X네이버클라우드 AI 포텐데이 해커톤 `🥇 MVP 1Pick`, `🏆 고도화 트랙 1등` <br>
> **관련 포스팅** : [AI 포텐데이 데모데이 - “일상을 바꾸는 AI 기반 서비스 5가지를 소개합니다”|작성자 NAVER Cloud](https://blog.naver.com/n_cloudplatform/223992345150)  <br>


<img width="1920" height="1080" alt="표지" src="https://github.com/user-attachments/assets/d48a4396-3e69-4e90-a96f-e2923d144ea2" />

## 🔧 기술 스택
<p align=left>
  <img src="https://img.shields.io/badge/Java (JDK 21)-437291?style=flat&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Boot (3.2.4)-6DB33F?style=flat&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/HyperCLOVA X-E151E3?style=flat&logo=naver&logoColor=white">
  <img src="https://img.shields.io/badge/CLOVA Studio-1435CE?style=flat&logo=naver&logoColor=white">
</p>
<p align=left>
  <img src="https://img.shields.io/badge/Naver Cloud Server-03C75A?style=flat&logo=naver&logoColor=white">
  <img src="https://img.shields.io/badge/nginx-009639?style=flat&logo=nginx&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white">
</p>

## 🏗️ 아키텍쳐
> Server Architecture
> <img width="1920" height="1080" alt="Server Architecture" src="https://github.com/user-attachments/assets/ce24ac69-4523-42d1-8a3d-18e1eefcdbe7" />

> AI Workflow
> <img width="1920" height="1080" alt="AI Workflow" src="https://github.com/user-attachments/assets/692073e9-b580-41aa-8873-51cb63b9360b" />

## 📁 디렉토리 구조
본 프로젝트는 다음과 같은 멀티모듈 기반 계층형 아키텍처로 구성되어 있습니다.

1인 개발 및 빠른 개발 속도를 위해 **도메인 주도 설계(DDD)** 의 핵심 원칙인 **도메인 계층의 독립성** 을 확보하면서도, 실용적인 관점에서 유연하게 구조화되었습니다.
<pre> 
server/
├── common/               # 공통 모듈 (예외, 유틸 등)
│
├── user-api/             # 사용자용 API 서버
│   ├── controller/       # REST API 컨트롤러
│   ├── dto/              # 요청/응답 전용 DTO
│   ├── service/          # 애플리케이션 서비스 (트랜잭션, 도메인 서비스 호출)
│   └── Application.java 
│
├── domain/               # 핵심 도메인 로직
│   ├── model/            # 도메인 모델 
│   ├── repository/       # JPA Repository 인터페이스
│   └── service/          # 도메인 서비스 (비즈니스 로직 담당)
│
└── external/             # 외부 API 연동 모듈 
</pre>
