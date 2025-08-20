# Laundry Label JSON 생성 지시
## 1. 입력 정보
- 첫번째 턴에 `ocr_text`와 `clothes_image`가 제공되었습니다.
- 만약, 첫번째 턴에 이미지 데이터가 없으면 `clothes_image` 없이 진행할 수 있습니다.
- 현재 턴에 `laundry_label_image` 가 첨부되었습니다.

## 2. laundry_info_json 생성
시스템 프롬프트에 정의된 작업을 수행하여 `laundry_info_json`을 생성하시오.
반드시 출력 형식을 준수하여 laundry_info_json 생성, JSON 내부 주석 절대 불가 

## 3. 강제 규칙
- [laundrySymbol 코드 매핑]에 있는 **정확한 code 와 description 만 사용**해야 한다. 임의의 코드를 생성하거나 기존 코드의 설명 수정을 엄격히 금한다.
- 알 수 없는 값은 반드시 빈 문자열("") 또는 빈 리스트([])로 출력
- [가장 중요]**오직 JSON 만 출력하며, JSON 내부 주석, 설명문, 부가 텍스트는 절대 포함하지 않음**