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
**도메인 중심의 설계**
- 도메인(domain) 패키지에 비즈니스 핵심 로직을 모음.
- 외부 의존성(DB, 프레임워크 등)으로부터 도메인 로직을 보호하여, 비즈니스 로직이 기술적 구현에 영향을 받지 않도록 함.

**Repository 추상화** <br>
domain.repository에서 인터페이스를 정의하고 infra에서 구현함으로써,
- 도메인 계층이 특정 데이터 접근 기술에 종속되지 않음.
- 데이터베이스나 외부 시스템 변경 시 도메인 로직 수정 없이 infra 계층만 수정하면 된다.
- 실제 DB 대신 mock repository를 사용할 수 있어 테스트가 용이해진다.<br>

**관심사의 명확한 분리**
- interfaces: 외부와의 통신을 담당 (HTTP 요청/응답, DTOs)
- domain: 핵심 비즈니스 규칙과 로직
- infra: 기술적 구현과 외부 시스템 연동<br>

**유스케이스 중심의 설계**
- domain.usecase 패키지를 통해 비즈니스 요구사항을 명확하게 표현함.
- 각 유스케이스는 도메인 엔티티들을 조합하여 하나의 완결된 비즈니스 기능을 구현한다.


## ERD 설계

<img width="600" alt="image" src="https://github.com/user-attachments/assets/f46f2f2a-1464-45dd-9c42-55aa86dd2359" />

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
* LECTURE와 SCHEDULE 테이블 분리<br>
    * 하나의 강의(LECTURE)가 여러 일정(SCHEDULE)을 가질 수 있는데, 이 경우 강의 정보(TITLE, INSTRUCTOR 등)가 여러 번 중복 저장됨<br> 
* SCHEDULE에 실시간 여석 컬럼 제거, 대신 현재 신청자 컬럼 추가<br> 
    * 동시성 제어 시 업데이트해야 할 컬럼이 늘어나 성능 저하 가능성 존재<br> 
* REGISTRATION 테이블에 LECTURE_ID 컬럼 추가 (반정규화)<br> 
    * 강의 정보 조회 시 JOIN 연산이 감소하여 쿼리 성능 향상을 기대하였고, 특히 수강신청 내역 조회 API에서 성능상 이점 기대<br> 
* REGISTRATION 테이블에 (SCHEDULE_ID, USER_ID) 복합 유니크 제약조건 설정<br> 
    * 데이터베이스 레벨에서 한 사용자가 동일한 스케줄에 중복 등록하는 것을 원천적으로 차단<br> 
    * 여러 사용자가 동시에 수강신청을 시도할 때 발생할 수 있는 race condition을 방지<br> 
    * (SCHEDULE_ID, USER_ID)에 대한 복합 유니크 인덱스를 활용한 빠른 중복 체크 가능<br> 
    ```
    시간순으로:
    1. 사용자A: INSERT INTO REGISTRATION (USER_ID=1, SCHEDULE_ID=1) 시도
    2. 사용자B: INSERT INTO REGISTRATION (USER_ID=1, SCHEDULE_ID=1) 시도
    3. 데이터베이스: 유니크 제약조건 위반으로 두 번째 INSERT 실패
    ```

## API 명세
1) 날짜별 특강 조회
* endpoint : GET /lectures/available?date={date}
* 특강을 신청하고자 하는 날짜(date)에 존재하는 특강을 보여준다.
    * totalSeats(정원)와 currentCount(신청인원)로 신청 가능 여부를 알 수 있다.
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
* 유저 아이디와 신청하고자 하는 특강일정의 아이디를 입력하면 신청이 완료되고, 신청 내용을 보여준다.
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
* 유저 아이디로 신청한 강의 목록을 보여준다.
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
            "lectureTitle": "2주차 멘토링",
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


    public Optional<Schedule> findScheduleWithLockById(Long scheduleId) {
        Schedule result = queryFactory
                .selectFrom(schedule)
                .where(schedule.id.eq(scheduleId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) //비관적 락
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
```
PESSIMISTIC_READ를 적용하면 어떻게 되냐고?
```
// 동시 접근 시나리오 (currentCount가 29인 상황)
Transaction A: SELECT * FROM schedule ... (공유 락, currentCount = 29 확인)
Transaction B: SELECT * FROM schedule ... (공유 락, currentCount = 29 확인)

// 두 트랜잭션 모두 29 < 30 조건을 통과하고 등록을 시도
Transaction A: UPDATE schedule SET current_count = 30 ...
Transaction B: UPDATE schedule SET current_count = 31 ... // 정원 초과 발생!
```
읽기를 허용하기 때문에 여러 트랜잭션이 동시에 29명이라고 읽을 수 있고, 결과적으로 정원인 30명을 초과하는 상황이 발생할 수 있다.

```
// 동시 접근 시나리오 (currentCount가 29인 상황)
Transaction A: SELECT * FROM schedule ... FOR UPDATE (배타적 락 획득)
Transaction B: 락 획득 대기...

Transaction A: currentCount 확인 및 증가 후 커밋
Transaction B: 락 획득 -> currentCount가 30임을 확인 -> CapacityExceededException 발생
```
PESSIMISTIC_WRITE을 사용하면 읽는 시점부터 배타적 락을 획득하여 다른 트랜잭션의 접근을 원천 차단하고, 정원 검사와 증가가 원자적으로 실행될 수 있다.

이렇게 비관적 락을 적용함으로써, Race Condition 방지, 데이터 정합성(30명 정원 초과 방지 보장), Lost Update 방지(동시 수정으로 인한 데이터 손실 방지), 트랜잭션 격리(다른 트랜잭션의 간섭 차단)을 이룰 수 있다.<br>
이 프로젝트는 정확히 30명 정원을 맞춰야하고, 이에 따라 동시 접근이 많을 수 있으며, 데이터 정합성이 사용자 경험보다 중요하므로 비관적 락을 선택하였다.

<img width="500" height="350" alt="image" src="https://github.com/user-attachments/assets/7a023472-1a74-4e04-8c36-74630b2fa3b5" />
<img width="500" height="350" alt="image" src="https://github.com/user-attachments/assets/b857512e-e2b0-4684-8361-20555afff7d1" />




## 테스트
1. 단위 테스트 (Mock)
2. 통합 테스트 (h2 db)
3. E2E 테스트 (MySql db)

