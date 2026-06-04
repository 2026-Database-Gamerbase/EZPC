# 🎮EZPC🖥 - PC방 통합 관리 시스템 프로젝트
2026-1 데이터베이스 과목을 수강하며 진행한 프로젝트입니다.
<br/><br/>

## 💁‍♂️ 프로젝트 팀원
<table>
  <tr height="160px">
    <td width="300px" align="center">
      <a href="https://github.com/EPSIHYEON">
        <img height="150px" width="150px" src="https://avatars.githubusercontent.com/EPSIHYEON" />
      </a>
    </td>
    <td width="300px" align="center">
      <a href="https://github.com/HongYeonLee">
        <img height="150px" width="150px" src="https://avatars.githubusercontent.com/HongYeonLee" />
      </a>
    </td>
    <td width="300px" align="center">
      <a href="https://github.com/CinnaPie">
        <img height="150px" width="150px" src="https://avatars.githubusercontent.com/CinnaPie" />
      </a>
    </td>
    <td width="300px" align="center">
      <a href="https://github.com/ChunHajin">
        <img height="150px" width="150px" src="https://avatars.githubusercontent.com/ChunHajin" />
      </a>
    </td>
  </tr>
  <tr height="30px">
    <td align="center">
      <a href="https://github.com/EPSIHYEON">
        <b>박시현</b>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/HongYeonLee">
        <b>이홍연</b>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/CinnaPie">
        <b>장은서</b>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/ChunHajin">
        <b>천하진</b>
      </a>
    </td>
  </tr>
</table>

<br/><br/>

## 📝 규칙

#### 커밋 컨벤션
**커밋 / PR에 공통 적용**되며, 작업 구분의 기본 틀입니다. **전체 소문자 작성**을 원칙으로 합니다.

| name | description |
| --- | --- |
| feat | 새로운 기능 추가 또는 기존 기능의 의미 있는 확장 |
| fix | 버그, 오류, 잘못된 동작 수정 |
| docs | 문서만 변경 (코드 동작 변화 없음) |
| refactor | 외부 동작은 유지한 채 내부 구조 개선 |
| test | 테스트 코드 추가 또는 테스트 관련 변경 |
| chore | 기능과 무관한 작업 또는 설정·운영 정리 |
| ci | CI 관련 작업 |
| build | 빌드 및 배포 설정 |
| perf | 성능 개선 |

<br>

## 🚀 시작하기

### 1. 데이터베이스 설정

**HeidiSQL**에 접속한 후, `src/sql` 경로의 쿼리를 아래 순서대로 실행합니다.

| 순서 | 파일 |
|:---:|---|
| 1 | DB 생성 쿼리 |
| 2 | 권한 쿼리 |
| 3 | 로그인 트리거 쿼리 |
| 4 | 리뷰 트리거 쿼리 |
| 5 | 주문 트리거 쿼리 |
| 6 | 충전 프로시저 쿼리 |
| 7 | 더미 데이터 생성 쿼리 |

### 2. DB 연결 설정

`src/db/DatabaseConnector.java`를 열어 본인 환경에 맞게 수정합니다.

```java
private static final String URL      = "jdbc:mysql://IP주소/EZPC";
private static final String ROOT_USER = "root";
private static final String ROOT_Password = "";
```

### 3. 실행

`src/Main.java`를 실행합니다.


## EZPC 전체 동작 흐름

---

## 1. DB 계정 구조

| 계정 | 권한 | 사용 시점 |
|------|------|-----------|
| `ezpc_auth` | `pc_member` SELECT만 | 로그인 화면 — 인증 전용, 즉시 해제 |
| `ezpc_user` | 사용자 업무 테이블 + 프로시저 실행 | 로그인 성공 후 — UserController 전체 생애 |
| `ezpc_owner` | `EZPC.*` ALL | 로그인 성공 후 — OwnerController 전체 생애 |

> 계정 생성 쿼리: `db 권한 설정 쿼리.sql` (HeidiSQL에서 root로 실행)

---

## 2. 앱 시작

```
Main.java
  └─ SwingUtilities.invokeLater()
       └─ new LoginController()
            ├─ DatabaseConnector.getAuthConnection()  →  ezpc_auth 연결
            └─ LoginView 표시
```

---

## 3. 로그인 처리

```
LoginView — 로그인 버튼 클릭
  └─ LoginController.handleLogin()
       ├─ PC_MemberService.login(id, pw)
       │    └─ PC_MemberDAOImpl.findByID()
       │         └─ ezpc_auth 연결로 pc_member SELECT
       │
       ├─ 실패
       │    └─ LoginView.setStatusMessage("아이디 또는 비밀번호가 올바르지 않습니다.")
       │
       └─ 성공
            ├─ authConn.close()                              ← 인증 전용 연결 즉시 해제
            ├─ DatabaseConnector.getConnection(memberType)   ← 역할별 연결 새로 생성
            │
            ├─ memberType = "owner"  →  new OwnerController(ownerConn, member).start()
            └─ memberType = "user"   →  new UserController(userConn, member).start()
```

---

## 4. 회원가입 처리

```
LoginView — 회원가입 버튼 클릭
  └─ LoginController.handleSignUp()
       └─ SignUpView 표시
            └─ 회원가입 버튼 클릭
                 ├─ 비밀번호 확인 불일치 → SignUpView.setStatusMessage("비밀번호 불일치")
                 └─ 일치
                      └─ PC_MemberService.signUp(id, pw, name)
                           ├─ 중복 ID 검사 (findByID)
                           └─ insertMember()  →  member_type = "user" 고정, grade = "bronze"
```

> 회원가입은 `ezpc_auth` 연결을 재사용하므로 별도 DB 연결 불필요  
> (단, `ezpc_auth`에 `pc_member INSERT` 권한이 없다면 회원가입 전용 연결을 별도로 사용)

---

## 5. 사용자 흐름 (ezpc_user 계정)

```
UserController(userConn, member)
  └─ 모든 DAO를 userConn으로 생성
```

### 5-1. 지점 선택

```
UserBranchSelectView 표시
  └─ 지점 버튼 클릭
       └─ PcCafeService.getAllPcCafes()
            └─ PcCafeDAOImpl.findAll()  →  pc_cafe SELECT
```

### 5-2. 좌석 선택

```
UserSeatSelectView 표시
  ├─ CustomerService.getCustomersInPcCafe(pcCafeId)  →  현재 이용 중인 좌석 목록
  └─ 좌석 선택
       └─ CustomerService.checkIn(customer)
            ├─ CustomerDAOImpl.insertCustomer()   →  customer INSERT
            └─ LogDAOImpl.insertLog()             →  use_log INSERT (login_time 기록)
```

### 5-3. 이용 중 (UserMainDashboardView)

#### 이용권 충전
```
충전 버튼 클릭
  └─ ChargeService.recordCharge(charge)
       └─ ChargeDAOImpl.chargeByCustomer()
            └─ CALL charge_by_customer(pc_cafe_id, seat_num, member_id, ticket_time)
                 ├─ charge INSERT                        (충전 기록)
                 ├─ customer.remain_time UPDATE          (현재 손님 잔여 시간)
                 ├─ pc_member.total_payment_amount UPDATE (누적 결제 금액, 회원만)
                 └─ pc_member.grade_type UPDATE          (등급 재산정, 회원만)
```

#### 음식 주문
```
음식주문 버튼 클릭
  └─ UserFoodOrderView 표시
       ├─ FoodService.getAllFoods()             →  음식 메뉴 조회
       ├─ StockService.getStock(pcCafeId)       →  재고 확인
       └─ OrderService.placeOrder(order)
            ├─ EventScheduleDAOImpl.findCurrentOrderPaymentRate()  →  이벤트 할인율 조회
            ├─ food_order INSERT                (주문 기록)
            └─ StockDAOImpl.decreaseStock()     (재고 차감)
```

#### 리뷰 작성
```
리뷰 버튼 클릭  (회원만 가능)
  └─ UserReviewManageView 표시
       └─ ReviewService.addReview(review)
            └─ ReviewDAOImpl.insert()           →  review INSERT
```

#### 로그아웃 (퇴실)
```
로그아웃 버튼 클릭
  └─ CustomerService.checkOut(customer)         ← 트랜잭션
       ├─ LogDAOImpl.updateLogoutTime()         →  use_log.logout_time UPDATE
       ├─ LogDAOImpl.findLatestLogoutLog()      →  로그인/아웃 시간 조회
       ├─ 사용 시간 계산 (logout - login, 분 단위)
       ├─ PC_MemberDAOImpl.updateRemainTimeAfterUse()  →  remain_time 차감 (회원만)
       └─ CustomerDAOImpl.deleteCustomer()      →  customer DELETE
```

---

## 6. 운영자 흐름 (ezpc_owner 계정)

```
OwnerController(ownerConn, member)
  └─ 모든 DAO를 ownerConn으로 생성
       └─ OwnerMainFrameView 표시  (탭 5개)
```

| 탭 | 서비스 | 주요 기능 |
|----|--------|-----------|
| 좌석 모니터링 | `CustomerService` | 지점별 현재 이용 중인 손님 조회 |
| 매출 통계 | `SalesReportService` | 월별·이벤트별·피크타임별 매출 조회 |
| 음식 재고 | `StockService` | 지점별 재고 조회 및 수정 |
| 직원 관리 | `EmployeeService` | 직원 추가·수정·삭제 |
| 회원 관리 | `PC_MemberService` | user 타입 회원 조회 (owner 계정 제외) |

---

## 7. 계층별 역할 요약

```
View        — 화면 표시 + 버튼 리스너 노출만 담당, 비즈니스 로직 없음
Controller  — View와 Service 연결, 화면 전환, DB 연결 생성·관리
Service     — 비즈니스 로직 처리
DAOImpl     — SQL 실행, 외부에서 주입받은 conn만 사용
```

---

## 8. DatabaseConnector 사용 규칙

- `DatabaseConnector`를 직접 호출하는 곳은 **`LoginController` 하나뿐**
- 모든 DAOImpl은 **생성자로 주입받은 `conn`만 사용** (내부에서 직접 연결 생성 금지)
- 역할별 `conn`은 Controller 생성 시 한 번만 만들어지고 해당 Controller 생애 동안 유지

```java
// LoginController에서만 호출
Connection authConn  = DatabaseConnector.getAuthConnection();           // 로그인용
Connection roleConn  = DatabaseConnector.getConnection(memberType);     // 역할별

// Controller 내부에서 DAO 생성
EmployeeDAO employeeDao = new EmployeeDAOImpl(roleConn);
GradeDAO    gradeDao    = new GradeDAOImpl(roleConn);
TicketDAO   ticketDao   = new TicketDAOImpl(roleConn);
// ... 나머지 동일
```
