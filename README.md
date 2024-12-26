# Clean-Layered Architecture로 특강 신청 서비스 구현하기

## 요구사항

1️⃣ **(핵심)** 특강 신청 **API**
- 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
- 동일한 신청자는 동일한 강의에 대해서 한 번의 수강 신청만 성공할 수 있습니다.
- 특강은 선착순 30명만 신청 가능합니다.
- 이미 신청자가 30명이 초과 되면 이후 신청자는 요청을 실패합니다.

**2️⃣ 특강 신청 가능 목록 API** 
- 날짜별로 현재 신청 가능한 특강 목록을 조회하는 API 를 작성합니다.
- 특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기 전 목록을 조회해 볼 수 있어야 합니다.

3️⃣  **특강 신청 완료 목록 조회 API**
- 특정 userId 로 신청 완료된 특강 목록을 조회하는 API 를 작성합니다.
- 각 항목은 특강 ID 및 이름, 강연자 정보를 담고 있어야 합니다.

## 핵심 문제 해결 전략
- 동시성 제어를 통해 정확한 신청자 수를 관리한다.
- 중복 신청 방지를 위한 유니크 제약 조건을 설계한다.

## 프로젝트 구조
```
com.hhplus.cleanArchitecture
├── interfaces
│   ├── common
│   └── lecture
│       ├── controller
│       └── dto       
├── domain
│   ├── entity
│   ├── exception
│   ├── model
│   ├── repository
│   └── usecase
└── infra
    ├── config
    ├── lecture
    ├── registration
    └── schedule
```

## ERD 설계

### 테이블 구조

### 테이블 관계
1. LECTURE - SCHEDULE 관계
    - 1:N 관계 (One-to-Many)
    - 하나의 LECTURE는 여러 SCHEDULE을 가질 수 있음
    - SCHEDULE.LECTURE_ID는 LECTURE.LECTURE_ID를 참조하는 외래키
2. SCHEDULE - REGISTRATION 관계
    - 1:N 관계 (One-to-Many)
    - 하나의 SCHEDULE은 여러 REGISTRATION을 가질 수 있음
    - REGISTRATION.SCHEDULE_ID는 SCHEDULE.SCHEDULE_ID를 참조하는 외래키
3. LECTURE - REGISTRATION 관계
    - 1:N 관계 (One-to-Many)
    - 하나의 LECTURE는 여러 REGISTRATION을 가질 수 있음
    - REGISTRATION.LECTURE_ID는 LECTURE.LECTURE_ID를 참조하는 외래키
4. 특별한 제약조건
    - REGISTRATION 테이블의 (SCHEDULE_ID, USER_ID) 조합에 UNIQUE 제약조건
    - SCHEDULE.CAPACITY의 기본값은 30
    - SCHEDULE.CURRENT_COUNT의 기본값은 0

### 설계 이유
- 강의와 강의 일정 테이블을 분리하여 
- 강의 일정 테이블에 실시간 수강 여석 컬럼 제거
- 강의 신청 내역 테이블에 강의 식별 ID 추가

## API 명세
1) 날짜별 특강 조회
* endpoint : GET /lectures/available?date={date}
* Response
  ```json
  {
      "success": true,
      "data": [
          {
              "lectureId": "1",
              "title": "크리스마스 기념 특강",
              "instructor": "허재",
              "lectureDate": "2024-12-26",
              "currentCount": 0,
              "totalSeats": 30
          }
      ],
      "message": null
  }
  ```
2) 특강 신청
* endpoint : POST /lectures/{userId}/register
* Request
  ```json
  {
    "scheduleId":1 
  }
  ```
* Response

  ```json
  {
    "success": true,
    "data": {
        "registrationId": "9",
        "userId": "7",
        "registeredAt": "2024-12-26",
        "lectureId": "1",
        "lectureTitle": "크리스마스 기념 특강",
        "instructor": "허재",
        "lectureDate": "2024-12-26"
    },
    "message": null
  }
  ```


3) 신청한 특강 조회
* endpoint : GET /lectures/{userId}/registered
* Response
  ```json
  {
    "success": true,
    "data": [
        {
            "registrationId": "9",
            "userId": "7",
            "registeredAt": "2024-12-26",
            "lectureId": "1",
            "lectureTitle": "크리스마스 기념 특강",
            "instructor": "허재",
            "lectureDate": "2024-12-26"
        },
        {
            "registrationId": "8",
            "userId": "7",
            "registeredAt": "2024-12-26",
            "lectureId": "2",
            "lectureTitle": "근거가 있는 강의",
            "instructor": "하헌우",
            "lectureDate": "2024-12-30"
        }
    ],
    "message": null
  }
  ```
## 동시성 제어
### 데이터베이스 락의 필요성
이번 프로젝트는 분산 환경 즉, 서버가 여러대인 환경이다!<br>
이전 프로젝트에서 사용한 ReentrantLock과 ConcurrentHashMap으로는 더이상 동시성 제어가 불가능하다.<br>
얘네들은 단일 JVM 내의 메모리에서만 동작하기 때문에, 서버가 여러 대인 분산환경에서는 각 서버의 JVM이 독립적으로 동작하여
한 서버의 락이 다른 서버에는 전혀 영향을 미치지 못한다.<br>
```
서버A: ReentrantLock lock = new ReentrantLock();
서버B: ReentrantLock lock = new ReentrantLock();
```
위와 같이 각 서버는 독립적인 락 인스턴스를 가지고, 한 서버에서의 락 호출이 다른 서버에 전파되지 않는다.<br>
=> 동시성을 제어하는 새로운 방식이 필요하다.<br>
<br>
여러 대의 서버가 한 자원에 접근 할 때 `데이터베이스`가 중앙에서 락을 관리하므로 모든 서버에 일관되게 적용이 가능하다.<br>
데이터베이스로 락을 제어하는 방식은 크게 낙관적 락, 비관적 락 두가지가 있다.<br>

### 낙관적 락
낙관적 락은 동시성 문제가 많이 발생하지 않을 것으로 보일 때 사용한다.<br>
락 획득을 위한 대기 시간은 없지만 충돌 시 롤백 후 재시도하며 이로 인해 성능이 저하된다.<br>
주로 version이라는 컬럼을 이용하여 조회 시점에 version 값을 함께 읽어오고, 수정을 시도할 때 where 절에 version 조건을 포함한다.<br>
다른 트랜잭션이 먼저 수정하면 version이 증가하여 where 조건이 불일치하여 실패하게 되는 로직이다.<br>
```
[최초 상태]
ID: 1, currentCount: 29, version: 1

트랜잭션 A: SELECT * FROM schedule WHERE id = 1; // version = 1
트랜잭션 B: SELECT * FROM schedule WHERE id = 1; // version = 1

트랜잭션 A: UPDATE schedule SET current_count = 30, version = 2 WHERE id = 1 AND version = 1; // 성공
트랜잭션 B: UPDATE schedule SET current_count = 30, version = 2 WHERE id = 1 AND version = 1; // 실패
```
읽기가 많고 쓰기가 적은 경우, 데이터 충돌 가능성이 낮은 경우, 긴 대기 시간이 허용되지 않는 경우(예시: 게시글 수정, 개인정보 업데이트 등) 낙관적 락을 사용한다.

### 비관적 락
비관적 락은 동시성 문제가 많이 발생할 것으로 예측될 때 사용된다. <br>
앞에 사용 중인 트랜잭션(락)이 있다면, 그것이 끝날 때까지 영원히 대기한다.
조회 시점에 해당 레코드(동시에 접근할 확률이 높은 자원)에 대한 배타적 락을 획득하고, 트랜잭션이 완료될 때까지 다른 트랜잭션의 접근을 차단한다.
락 획득을 시도하는 다른 트랜잭션은 대기 상태이다.
```
// 주요 LockMode 유형
PESSIMISTIC_READ    // 공유 락 (다른 트랜잭션의 읽기는 허용)
PESSIMISTIC_WRITE   // 배타적 락 (읽기/쓰기 모두 차단)
PESSIMISTIC_FORCE_INCREMENT  // 배타적 락 + 버전 증가
```
비관적 락은 높은 동시성 + 데이터 정합성이 중요한 경우 사용된다.<br>
ex) 좌석 예약 시스템, 재고 관리, 특강/수강 신청, 포인트/머니 차감<br>
외부 API 호출 등으로 긴 시간이 소요되거나 읽기가 많은 경우에는 비관적 락을 사용하는 것은 적절하지 않다. <br>

이 프로젝트에서는 특강 신청 service의 `findScheduleWithLockById()` 부분에 `PESSIMISTIC_WRITE`를 사용하여 비관적 락을 적용하였다.
```java
@Transactional
public RegisterInfo register(RegisterCommand command) {
    // 1. 락 획득과 동시에 데이터 조회
    Schedule schedule = scheduleRepository.findScheduleWithLockById(command.getScheduleId())
            .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

    // 2. 안전한 검증
    if (schedule.isCapacityFull()) {
        throw new CapacityExceededException("정원이 초과되었습니다.");
    }

    // 3. 안전한 수정
    schedule.increaseCurrentCount();
}
```
이렇게 비관적 락을 적용함으로써, Race Condition 방지, 데이터 정합성(30명 정원 초과 방지 보장), Lost Update 방지(동시 수정으로 인한 데이터 손실 방지), 트랜잭션 격리(다른 트랜잭션의 간섭 차단)을 이룰 수 있다.<br>
이 프로젝트는 정확히 30명 정원을 맞춰야하고, 이에 따라 동시 접근이 많을 수 있으며, 데이터 정합성이 사용자 경험보다 중요하므로 비관적 락을 선택하였다.






## 테스트

