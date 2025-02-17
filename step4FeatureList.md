## 4단계 ATDD 시나리오 정리

### Feature: 지하철 노선 관리 기능<br>

###기능 추가
* 노선 내 지하철역 삭제 기능 추가
  * 삭제되는 역이 종점일 경우 이전의 역이 종점이 됨
  * 삭제되는 역이 시작점일 경우 다음의 역이 시작점이 됨
  * 역이 삭제된 후에는 이전과 다음 역이 하나의 구간으로 병합
  * 노선 내 구간이 1개만 존재할 경우 역 삭제요청시 에러발생
  * 노선 내 존재하지 않는 역을 삭제요청 시 에러 발생

---

Scenario: 지하철 노선 내 역 삭제요청 성공<br>
Given: 지하철 노선 및 구간 등록<br>
When: 등록된 노선 내 구간에서 특정 역 삭제 요청<br>
Then: 노선 내 지하철역 삭제 성공<br>
When: 등록된 노선 내 지하철역 목록 조회<br>
Then: 삭제된 역 제외한 목록 확인<br>

Scenario: 지하철 노선 내 역 삭제요청 실패 (구간 1개만 존재)<br>
Given: 지하철 노선 및 구간 1개만 등록<br>
When: 등록된 노선 내 구간에서 특정 역 삭제 요청<br>
Then: 노선 내 1개의 구간만 존재하므로 실패<br>

Scenario: 지하철 노선 내 역 삭제요청 실패 (미 존재 역)<br>
Given: 지하철 노선 및 구간 등록<br>
When: 등록된 노선 내 구간에서 미 존재 역 삭제 요청<br>
Then: 노선 내 구간에 역이 존재하지 않으므로 실패<br>