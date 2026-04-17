# Phân Tích Chuyên Sâu: Kiến Trúc Multi-Module Maven & DDD
**Dự án:** Customer Management & Auth Server

---

## 1. Tầm Nhìn & Triết Lý Thiết Kế (The Vision)

Dự án này là một nguyên mẫu (prototype) minh họa cho việc xây dựng hệ thống Backend cấp doanh nghiệp, áp dụng các tư tưởng thiết kế phần mềm hiện đại nhất:
* **Domain-Driven Design (DDD):** Tập trung vào việc mô hình hóa lõi nghiệp vụ.
* **Clean Architecture:** Phân tách ranh giới rõ ràng giữa các mối quan tâm (Separation of Concerns).
* **Physical Boundary Enforcement (Ranh giới vật lý):** Sử dụng cơ chế Multi-Module của Maven để ép buộc các quy tắc phụ thuộc ở cấp độ trình biên dịch (Compile-time).

Thay vì gom tất cả code vào một khối (Monolith) và chia package (ranh giới logic lỏng lẻo), dự án sử dụng các module Maven độc lập. Điều này đóng vai trò như những "bức tường thép", ngăn chặn triệt để tình trạng gọi chéo code sai nguyên tắc (Spaghetti code) thường thấy khi dự án phình to.

---

## 2. Giải Phẫu Cấu Trúc Multi-Module (Module Anatomy)

Hệ thống được chẻ nhỏ thành 5 module Maven riêng biệt, tuân thủ nguyên lý **Dependency Inversion** (Đảo ngược phụ thuộc):

```text
analys-ddd-ca (Parent POM)
 ├── domain/       [Lõi Nghiệp Vụ - Không phụ thuộc ai]
 ├── service/      [Điều Phối - Phụ thuộc: domain]
 ├── controller/   [Giao Tiếp Web - Phụ thuộc: domain, service]
 ├── application/  [Điểm Khởi Chạy - Phụ thuộc: TẤT CẢ]
 └── authserver/   [Microservice Độc Lập - Spring Security/JWT]
 ```

 ### 2.1. Module `domain` (Trái tim hệ thống)
* **Vai trò:** Là lõi trung tâm chứa các thực thể nghiệp vụ (`Customer`), các định nghĩa hợp đồng hành vi (`CustomerService` interface) và cổng giao tiếp lưu trữ (`CustomerRepository` interface).
* **Phân tích:** Việc đặt các Interface tại đây là một ứng dụng xuất sắc của nguyên lý **Dependency Inversion (SOLID)**. Nó ép buộc các tầng bên ngoài (như `service` hay `infrastructure`) phải tuân theo "hợp đồng" do Domain đặt ra, từ đó đảo ngược chiều phụ thuộc truyền thống (thay vì Domain phải phụ thuộc vào Database).

### 2.2. Module `service` (Tầng Application / Use Case)
* **Vai trò:** Chứa `CustomerServiceImpl` (thực thi interface từ module domain). Lớp này nhận lệnh từ thế giới bên ngoài, gọi cơ sở dữ liệu thông qua repository, và điều phối các đối tượng Domain.
* **Phân tích:** Đóng vai trò là Use Case Orchestrator (Người điều phối nghiệp vụ). Bằng cách không dính dáng đến HTTP Request/Response, logic điều phối này hoàn toàn "vô hình" trước công nghệ giao tiếp. Nếu sau này hệ thống mở rộng, cần gọi qua gRPC, Message Queue (Kafka/RabbitMQ) hay CLI, module này vẫn được tái sử dụng 100%.

### 2.3. Module `controller` (Tầng Delivery / Presentation)
* **Vai trò:** Chứa `CustomerController`, định nghĩa các RESTful API Endpoints.
* **Phân tích:** Là lớp vỏ bọc ngoài cùng (Physical Web Boundary). Nó chỉ có một trách nhiệm duy nhất: Hứng HTTP Request, bóc tách dữ liệu, chuyển cho `service` xử lý, và đóng gói kết quả thành JSON trả về cho Client. Module này tuyệt đối không được chứa bất kỳ câu lệnh `if/else` nào liên quan đến luật kinh doanh.

### 2.4. Module `application` (Composition Root)
* **Vai trò:** Nơi chứa class khởi chạy chính mang annotation `@SpringBootApplication` và các cấu hình cơ sở dữ liệu (sử dụng H2 In-memory cho demo).
* **Phân tích:** Đóng vai trò là "Trạm lắp ráp" (Composition Root). Nó có nhiệm vụ nhìn thấy tất cả các module còn lại, gom chúng lại và để IoC Container của Spring Boot tự động Inject các Implementation (như Repository) vào đúng các Interface đang chờ sẵn. 

### 2.5. Module `authserver` (Identity Provider)
* **Vai trò:** Một mini-microservice chạy độc lập trên port `8083`, chuyên lo việc cấp phát và xác thực JWT Token.
* **Phân tích:** Tách biệt hoàn toàn logic bảo mật (Security/Authentication) khỏi logic nghiệp vụ (CRM). Đây là một thiết kế mang tư duy Microservices cực kỳ rõ nét, tránh việc nhồi nhét bộ lọc bảo mật vào bên trong lõi hệ thống khách hàng.

---

## 3. Luồng Thực Thi Dữ Liệu (Data Flow)

Luồng đi của một Request xuyên qua các module tuân thủ chặt chẽ nguyên tắc "Dependencies point inward" (Phụ thuộc hướng vào trong):

```text
[Client] (Postman/Browser)
   │ (HTTP POST /customers)
   ▼
[Controller Module] -> CustomerController.java
   │ (Hứng Request, bóc tách tham số, gọi Interface)
   ▼
[Service Module] -> CustomerServiceImpl.java
   │ (Khởi tạo Entity, thực thi Use Case, quản lý Transaction)
   ▼
[Domain Module] -> Customer.java & CustomerRepository (Interface)
   │ (Thỏa mãn hợp đồng lưu trữ)
   ▼
[Application Module / H2 Database] -> Thực thi Insert/Select dưới đáy
```
---

## 4. Phản Biện Kiến Trúc Thực Tế (Critical Architectural Review)

Mặc dù dự án sở hữu một lớp vỏ bọc vật lý (Multi-module) xuất sắc để ngăn chặn việc gọi chéo package, nhưng khi "soi" sâu vào mã nguồn, lõi kiến trúc vẫn bộc lộ **2 lỗ hổng nghiêm trọng** làm phá vỡ bản chất của Clean Architecture và DDD:

### 4.1. Ô Nhiễm Lõi Nghiệp Vụ (Framework Pollution)
Bên trong module `domain`, class `Customer.java` đang bị gắn chặt với các annotation của JPA (`@Entity`, `@Table`, `@Id`, `@Column`).
* **Hậu quả:** Tầng Domain – nơi được mệnh danh là "Trái tim" của hệ thống – lẽ ra phải là Pure Java (Java thuần túy), nay lại bị dính chặt vào công nghệ cơ sở dữ liệu quan hệ (Hibernate/SQL). Lõi nghiệp vụ đã bị rò rỉ và phụ thuộc vào hạ tầng bên ngoài. Nếu tương lai dự án muốn chuyển đổi sang NoSQL (như MongoDB) để tăng tốc độ truy xuất, toàn bộ module Domain sẽ phải đập bỏ và viết lại.

### 4.2. Mô Hình Miền Thiếu Máu (Anemic Domain Model)
Thực thể `Customer` hiện tại có constructor rỗng `public Customer() { }` và phơi bày toàn bộ các hàm `setter` (`setName`, `setJob`) ra public.
* **Hậu quả:** Lớp `Customer` bị giáng cấp thành một DTO (Data Transfer Object) vô tri thay vì một **Aggregate Root** thông minh. Bất kỳ thành phần nào (từ Controller hay Service) cũng có thể set dữ liệu bừa bãi vào Entity mà không vấp phải bất kỳ rào cản kiểm tra tính toàn vẹn (Validation) nào. Thực thể mất hoàn toàn khả năng tự bảo vệ trạng thái của chính nó.



** Đề xuất cải tiến kiến trúc:** 1. **Làm sạch Domain:** Loại bỏ hoàn toàn JPA annotation khỏi module `domain`. Lõi nghiệp vụ chỉ nên chứa các thuộc tính và các hành vi (Behavior methods) chứa business logic (ví dụ: `changeJob()`, `updateProfile()`).
2. **Tạo lớp hạ tầng:** Thiết lập một class `CustomerDbEntity` riêng biệt mang các annotation JPA nằm ở một module/tầng Infrastructure chuyên biệt.
3. **Ánh xạ dữ liệu:** Sử dụng Mapper (như MapStruct) để chuyển đổi dữ liệu qua lại giữa Domain Entity và DB Entity tại ranh giới của tầng Infrastructure.

---

## 5. Hướng Dẫn Vận Hành (Getting Started)

### 5.1. Yêu cầu môi trường
* Java JDK 11 hoặc 17+
* Apache Maven 3.x

### 5.2. Khởi chạy dự án
Do dự án sử dụng H2 Database (In-memory), hệ thống đã sẵn sàng chạy ngay mà không cần cài đặt CSDL vật lý bên ngoài.

```bash
# 1. Cài đặt và build toàn bộ các module từ thư mục root của dự án
mvn clean install

# 2. Di chuyển vào module Application (Module khởi chạy chính)
cd Application

# 3. Khởi chạy ứng dụng Spring Boot
mvn spring-boot:run
```

*Giao diện quản trị H2 Console có thể truy cập tại:* `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:Customer`)

### 5.3. REST API Endpoints (Cổng 8080)

| HTTP Method | Endpoint | Trách nhiệm |
| :--- | :--- | :--- |
| `GET` | `/customers` | Trích xuất danh sách toàn bộ khách hàng hiện có. |
| `GET` | `/customers/{id}` | Lấy thông tin chi tiết một khách hàng cụ thể theo định dạng UUID. |
| `POST` | `/customers?name={name}&job={job}` | Khởi tạo khách hàng mới vào hệ thống thông qua Service Orchestrator. |
| `DELETE`| `/customers/{id}` | Xóa khách hàng khỏi hệ thống dựa trên UUID. |

**Ví dụ cURL test luồng tạo mới:**
```bash
curl -X POST "http://localhost:8080/customers?name=MeThaiHuy&job=BackendDeveloper"
```
---
## 6. Giá Trị Doanh Nghiệp (Business Value) & Tầm Nhìn Dài Hạn

Tại sao chúng ta phải "chịu khổ" setup một kiến trúc Multi-module Maven lằng nhằng và chia cắt nhiều tầng như vậy thay vì viết mô hình MVC truyền thống cho nhanh?

* Bảo Vệ Mã Nguồn Khi Team Phình To (Scale-proof): Khi team dự án mở rộng lên hàng chục người, ranh giới vật lý của Maven ép buộc tất cả thành viên phải tuân thủ đúng luồng kiến trúc. Không ai có thể vô tình "đi tắt" hay import sai layer vì Compiler (Trình biên dịch) sẽ báo lỗi đỏ màn hình ngay lập tức.

* Khả Năng Tái Sử Dụng Tuyệt Đối (Reusability): Lõi nghiệp vụ (Domain) và Tầng điều phối (Service) hoàn toàn "mù" về giao thức HTTP. Nhờ vậy, ta có thể dễ dàng gắn thêm một cổng giao tiếp gRPC, một Kafka Consumer, hoặc một CLI tool tái sử dụng lại 100% logic hiện tại mà không phải sửa một dòng code nào ở lõi.

* Tính Tiến Hóa Của Hệ Thống (Evolutionary Architecture): Hệ thống được bảo vệ khỏi sự lỗi thời của công nghệ bề mặt. Việc chuyển đổi Database từ H2 sang PostgreSQL, hoặc gỡ bỏ Spring Web để dùng framework khác chỉ tác động đến các module lớp ngoài (Infrastructure/Controller), Trái tim hệ thống (Domain) vẫn đập bình thường.