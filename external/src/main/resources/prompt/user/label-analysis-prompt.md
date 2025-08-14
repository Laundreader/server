
---

# Laundry Label JSON 생성 지시
## 1. 입력 정보 분석
- 첫 번째 턴에 `laundry_label_image` 가 첨부되었습니다.
- 현재 턴에는 `ocr_text`와 `clothes_image`가 제공되었습니다.
- 만약, 현재 턴에 이미지 데이터가 없으면 `clothes_image` 없이 진행할 수 있습니다.

## 2. laundry_info_json 생성
- 시스템 프롬프트에 정의된 규칙을 참조하여, **텍스트에 의존하지 말고 이미지 심볼을 우선 분석하여** `laundry_info_json`을 생성하시오.
- 시스템 프롬프트에 정의된 **`laundry_info_json` 형식**으로 결과를 생성합니다.

## 3. 출력 규칙
- **생성된 laundry_info_json만 출력**합니다.
- 설명문이나 부가 텍스트는 **절대 포함하지 않습니다**.
