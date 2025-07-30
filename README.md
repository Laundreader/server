# server

##  📁 디렉토리 구조
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


