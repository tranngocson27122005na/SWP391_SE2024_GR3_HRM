# 01 — Đăng nhập & Phân quyền hệ thống

**Topic:** `auth`  
**Status:** Decisions locked (review 2026-07-21)  
**Date:** 2026-07-21  
**Tham chiếu init:** `01-init` §5, `04-architecture` §4–§7, `07-glossary`, ADR-0001  
**Mục đích:** Hình thành bài toán trước khi viết module docs (login/session, AuthFilter, đổi mật khẩu, admin ma trận).

> Discovery **không** mô tả URL, DDL, Controller, pseudocode. Chi tiết đó thuộc `docs/modules/` sau khi chốt.

---

## 1. Bài toán

Công ty cần một cổng vào duy nhất: người dùng đăng nhập bằng tài khoản hệ thống, sau đó chỉ được làm đúng việc thuộc **realm** của mình.

Hiện trạng thủ công (Excel) không có khái niệm “ai được vào màn nào”. Hệ thống số phải:

1. Xác thực đúng người (đăng nhập / đăng xuất / phiên / **đổi mật khẩu bản thân**).
2. Tách rõ **ADMIN (hệ thống)** và **USER (nghiệp vụ doanh nghiệp)** — không đan xen.
3. Với USER: quyền chức năng theo **chức danh** (`job_position`), phạm vi dữ liệu theo **DataScope** trên chức danh đó.
4. Từ chối rõ ràng khi cố vào việc không được phép (không hiểu nhầm là “chưa đăng nhập”).

Đây là **ưu tiên MVP #1** của Core Foundation — mọi module nghiệp vụ sau này phụ thuộc vào đây.

---

## 2. Ai là ai (Actor)

| Actor | Bản chất | Gắn hồ sơ nhân sự? | Việc được làm (ý) | Việc cấm |
|-------|----------|--------------------|-------------------|----------|
| **ADMIN** | Tài khoản hệ thống | Không | Quản trị tài khoản đăng nhập; **giữ catalog permission + ma trận** chức danh↔permission | Mọi nghiệp vụ doanh nghiệp |
| **USER** | Tài khoản nhân sự | Có, 1–1 với `employee` → `job_position` | Nghiệp vụ theo quyền chức danh; dữ liệu trong DataScope; đổi MK bản thân | Quản trị tài khoản hệ thống; sửa ma trận |

Chức danh nghiệp vụ là `job_position`, **không** phải system role.

**Self-Service** MVP: DataScope = SELF → chỉ xem dữ liệu của mình — **không** gồm nộp/duyệt đơn.

**Một `sys_user` đúng một system role** (`ADMIN` **hoặc** `USER`). **Không ngoại lệ.**

---

## 3. Đăng nhập — kết quả mong đợi

### 3.1 Điều kiện thành công

- Tài khoản tồn tại, đang mở (active).
- Mật khẩu khớp.
- Đúng một system role: `ADMIN` | `USER`.

### 3.2 Sau khi đăng nhập thành công

| Realm | Phiên cần mang | Không mang |
|-------|----------------|------------|
| ADMIN | Định danh + biết là ADMIN | employee, position, DataScope, permission nghiệp vụ |
| USER | Định danh + employeeId + positionId + departmentId + dataScope | Quyền admin hệ thống |

**Permission USER lúc kiểm tra request / home:** lấy theo `positionId` từ **cache ma trận đã reload** (xem §4.4) — không phụ thuộc snapshot cứng suốt đời phiên. Session giữ ngữ cảnh định danh + DataScope; tập permission theo chức danh luôn phản ánh ma trận hiện hành.

Sau login → **trang chủ** khác nhau theo realm (§5).

### 3.3 Đăng nhập thất bại

Thông báo chung (không lộ “user tồn tại nhưng sai MK”). Không tạo phiên.

### 3.4 Đăng xuất / hết hạn phiên

Xóa phiên; request sau phải đăng nhập lại. Timeout theo cấu hình Core.

### 3.5 Đổi mật khẩu bản thân (MVP — mọi người đã login)

- **Ai cũng dùng được** sau khi đăng nhập (ADMIN và USER).
- Thuộc **chức năng dùng chung / NFR trải nghiệm đăng nhập** — **không** đưa vào catalog permission hay ma trận `position_permission` (§4.5).
- Yêu cầu: xác thực mật khẩu hiện tại + mật khẩu mới hợp lệ; thông báo lỗi tiếng Việt khi sai.

---

## 4. Phân quyền — các lớp

### 4.1 Lớp A — Realm (system role)

- ADMIN chỉ công việc hệ thống.
- USER chỉ công việc nghiệp vụ.
- Vượt realm → **403** (không về login nếu đã có phiên).

### 4.2 Lớp B — Quyền chức năng (chỉ USER)

Permission dạng `{resource}:{ACTION}`. Nguồn: ma trận **chức danh ↔ permission**.

- **ADMIN** tạo/giữ catalog permission và gán/bỏ trên ma trận.
- Đổi ma trận → home/sidebar nhóm chức danh đổi theo.
- ADMIN **không** “có ALL quyền nghiệp vụ” qua lớp B.

### 4.3 Lớp C — DataScope (chỉ USER)

| DataScope | Ý nghĩa |
|-----------|---------|
| SELF | Chỉ dữ liệu của mình |
| DEPARTMENT | Trong phòng ban của mình |
| ALL | Toàn công ty (trong tài nguyên được phép) |

Lọc **trước** COUNT/LIMIT khi phân trang. ADMIN không áp DataScope nghiệp vụ.

### 4.4 Khi ADMIN đổi ma trận — quyết định kỹ thuật (đã đánh giá)

| Phương án | Ưu | Nhược |
|-----------|----|-------|
| Bắt re-login | Đơn giản nếu permission snapshot lúc login | Chậm hiệu lực; UX kém; không giống “admin vừa gán quyền” |
| Chỉ reload cache URL→permission, session vẫn snapshot cũ | Nhanh code nếu Filter chỉ nhìn session | **Sai**: user vẫn giữ quyền cũ / chưa thấy quyền mới |
| **Reload cache + tra quyền theo `positionId` mỗi lần (khuyến nghị)** | Đổi ma trận **có hiệu lực ngay**; khớp ý “chỉ cần reload cache”; gần hệ thống phân quyền thực tế | Filter/home phải đọc cache theo position, không tin snapshot permission cũ |

**Chốt MVP:** sau khi ADMIN lưu ma trận → `PositionPermissionMatrix.reload()`. Request sau (và home/sidebar) dùng cache mới theo `positionId`. **Không** bắt USER đăng nhập lại.

### 4.5 Việc dùng chung — **không** vào permission / ma trận

Mọi thao tác thuộc NFR hoặc chức năng **áp dụng cho tất cả người đã đăng nhập** (mọi realm) **không** seed vào `permission` và **không** gán trên ma trận. Ví dụ:

| Việc | Ghi chú |
|------|---------|
| Đăng nhập / đăng xuất | Public hoặc sau login |
| Trang chủ `/home` (lưới đã lọc theo realm) | Whitelist sau login |
| Đổi mật khẩu bản thân | MVP auth — mọi người |
| Trang lỗi 403/404/500 | Hệ thống |

Chỉ thao tác **nghiệp vụ phân biệt theo chức danh** mới vào ma trận.

---

## 5. Trang chủ (Home hub — lưới chức năng)

- **Không** bảng `menu` trong DB.
- **`/home` = một màn hình hub dạng lưới ô (grid)** — bố cục tham chiếu mock (ô bo góc, icon + nhãn, có thể có ô tìm kiếm phía trên). **Không dùng side-bar** trên màn này (hub thay cho cặp home+sidebar cũ).
- **Màu sắc / icon:** đội ngũ chọn tự do; **giữ bố cục lưới**, không bắt buộc palette của mock.
- **ADMIN:** **hai ô riêng** — Tài khoản → `/sys-user/list`; Ma trận phân quyền → `/permission-matrix/list` (mỗi ô một trang độc lập).
- **USER:** chỉ hiện ô tương ứng permission/module được phép (cache theo `positionId`). Ô module chưa mở MVP có thể ẩn hoặc disable.
- Sau reload ma trận → tập ô USER đổi theo (không bắt re-login).

*Ghi chú:* Màn hình bên trong từng module (list/detail…) dùng layout riêng (có thể có nav); **không** bắt buộc giống hub.

---

## 6. Từ chối truy cập (403)

| Tình huống | Ví dụ |
|------------|--------|
| USER thiếu permission | Công nhân mở màn chỉ dành HR |
| USER sai realm | USER sửa ma trận / CRUD tài khoản hệ thống |
| ADMIN sai realm | ADMIN mở hồ sơ NV / công / lương |

Chưa login / hết phiên → đăng nhập.

---

## 7. Phạm vi MVP của topic `auth`

### 7.0 Tách tài liệu module (đã chốt — phương án B)

| Docs module | Domain / folder | Nội dung MVP |
|-------------|-----------------|--------------|
| **common-auth** | `common` | Login, logout, session, AuthFilter, **home hub (grid, không sidebar)**, 403, đổi MK bản thân |
| **admin** | `admin` | Ma trận chức danh ↔ permission (gán/bỏ trên seed); `sys_user` list / khóa–mở / đổi MK giúp user |
| **org** | `org` | **Wave 1 (đã làm):** `employee:READ` + DataScope. **Wave 2 (discovery):** vòng đời theo HĐ — xem [`org/00-uc-catalog.md`](../org/00-uc-catalog.md). **Không** màn CRUD department / job-position. |

> Tên **org** (hồ sơ / tổ chức nhân sự) — tránh `authority` (dễ hiểu nhầm đăng nhập/phân quyền; phần đó thuộc common-auth + admin). Folder `docs/modules/authority/` cũ = lỗi thời, không dùng.

### 7.1 Module `org` — wave READ (đã khóa trước)

MVP wave 1: **xem hồ sơ / danh sách nhân viên** theo `employee:READ` + DataScope:

| DataScope | USER thấy gì |
|-----------|----------------|
| **SELF** | Chỉ hồ sơ **mình** (self-service) |
| **DEPARTMENT** | NV trong phòng mình |
| **ALL** | Toàn công ty (vd HR) |

**Không** làm trong wave 1: xem/CRUD `department`, xem/CRUD `job-position`, `employee` CUD/EXPORT.

### 7.2 Wave 2 — Hồ sơ & vòng đời theo Hợp đồng (supersede một phần §7.1 / §9)

Discovery review: [`docs/discovery/org/`](../org/00-uc-catalog.md).

| Hạng mục | Wave 1 (auth §9) | Wave 2 (đã chốt tranh luận 2026-07-23) |
|----------|------------------|----------------------------------------|
| Org | Chỉ `employee:READ` | Thêm CREATE/UPDATE emp; contract READ/CREATE/UPDATE/DELETE; dependent; bỏ dùng status/DELETE emp cho vòng đời |
| Tạo TK khi tạo emp | Không auto-tạo | **Wave 2:** ADMIN bù tay; **không** trigger/notify; kiểm tra **mỗi ngày làm việc** (ADR-0004, BR-ADM-SYNC-01) |
| Offboard / khóa TK | — | Cùng ADR-0004 (hai chiều, không notify) |
| Notification | — | **Treo** |
| Self-profile | `employee-self:READ` (vá) | **Dùng chung** whitelist |

### In scope (tóm tắt)

| Khối | Nội dung |
|------|----------|
| Login / logout / session | §3 |
| Đổi MK bản thân | Spec **common-auth** |
| AuthFilter + Matrix | §4 |
| **Home hub lưới** | §5 — common-auth; không sidebar |
| ADMIN — ma trận + sys-user | module **admin** |
| USER — xem NV | module **org** — chỉ `employee:READ` |

### Out of scope (MVP đợt này)

| Hạng mục | Lý do |
|----------|--------|
| Màn xem/CRUD `department` / `job-position` | Bỏ khỏi wave này |
| `employee:CREATE/UPDATE/DELETE/EXPORT` | Wave sau |
| Tạo TK khi HR tạo emp; CRUD catalog permission | Như đã khóa |
| Đơn từ / quên MK email / bảng menu | Treo |
| 4 role cũ | Đã loại (ADR-0001) |

---

## 8. Seed permission tối thiểu (đề xuất chốt)

**Nguyên tắc:** seed chỉ permission **nghiệp vụ** cần cho MVP đang làm; leave-* **không** seed MVP. ADMIN tạo/sửa thêm sau qua UI ma trận. DataScope nằm trên `job_position`, không nằm trong permission.

### 8.1 Catalog tối thiểu (MVP)

| Permission | Mục đích |
|------------|----------|
| `employee:READ` | Xem danh sách / chi tiết NV (phạm vi = DataScope chức danh) |
| `employee:CREATE` | Tạo NV (HR) — khi module employee mở |
| `employee:UPDATE` | Sửa NV (HR) |
| `employee:DELETE` | Soft-delete NV (HR-MGR) |
| `contract:READ` / `CREATE` / `UPDATE` | Hợp đồng (HR) khi module mở |
| `attendance:READ` / `IMPORT` | Công (khi module mở) |
| `payslip:READ` / `EXPORT` | Phiếu lương (khi module mở) |

*Chưa mở module → có thể chưa gán vào ma trận; catalog có thể seed trước hoặc thêm khi module Ready.*

### 8.2 Ma trận tối thiểu theo chức danh (đã seed trong `sql/0.seed_data.sql` trên DB `hrmdb`)

| Chức danh | data_scope | Permission tối thiểu MVP |
|-----------|------------|--------------------------|
| **HR-MGR** | ALL (3) | `employee:*`, `contract:READ/CREATE/UPDATE`, `attendance:READ/IMPORT`, `payslip:READ/EXPORT` |
| **HR-STF** | ALL (3) | `employee:READ/CREATE/UPDATE`, `contract:READ/CREATE`, `attendance:READ/IMPORT`, `payslip:READ` |
| **FAC-SUP** | DEPARTMENT (2) | `employee:READ`, `attendance:READ` |
| **FAC-WRK** | SELF (1) | `employee:READ`, `payslip:READ` |

> **Cùng `employee:READ`:** HR = ALL; giám sát xưởng = DEPARTMENT; công nhân = SELF — phân biệt bằng DataScope.

`permission.action` trong DB là **TINYINT code** (ADR-0002); `permission_name` vẫn chuỗi. **Không** seed `leave-request:*` trong MVP.

### 8.3 Không seed (dùng chung)

Login, logout, home, đổi mật khẩu, trang lỗi — §4.5.

---

## 9. Quyết định đã khóa

| # | Quyết định |
|---|------------|
| 1 | Không auto-tạo TK trong app org. Core MVP: ADMIN bù/khóa tay + **BR kiểm tra mỗi ngày làm việc** (onboard & offboard, không notify) — ADR-0004. |
| 2 | Đổi ma trận → **reload cache**; tra theo `positionId`; không bắt re-login. |
| 3 | Một `sys_user` đúng **1** role. |
| 4 | Đổi MK bản thân → Spec **common-auth**; không qua ma trận. |
| 5 | Catalog permission: seed; ADMIN MVP chỉ **gán/bỏ** trên ma trận. |
| 6 | Tách docs: **common-auth** \| **admin** \| **org** (chỉ `employee:READ` + DataScope). Không dùng tên `authority`. |
| 7 | Admin `sys_user`: list / khóa–mở / đổi MK giúp user — không CRUD đầy đủ. |
| 8 | Không màn xem/CRUD dept/job-position; không `employee:EXPORT`; self-service = xem hồ sơ mình. |
| 9 | `/home` = hub **lưới ô**, **không side-bar**; chỉ bắt buộc bố cục mock, màu tự do. ADMIN = **2 ô** (Tài khoản + Ma trận) → 2 trang riêng. |

---

## 10. Gap code (tóm tắt)

Filter/matrix theo role cũ; session thiếu position/dataScope; 403 lệch login; sidebar hard-code role cũ; home chưa phải grid hub; chưa đổi MK dùng chung; chưa `PositionPermissionMatrix`.

---

## 11. Bước tiếp

1. Cập nhật Spec common-auth (home hub) — rồi viết **org** (`employee:READ` only).
2. Seed MVP wave: ưu tiên gán `employee:READ` (+ DataScope); không cần seed department/job-position READ cho UI.
3. Employee CUD + contract → Discovery org wave 2 đã draft (`discovery/org/`); Spec sau khi BA duyệt catalogue.

---

## 12. Checklist chốt Discovery → Spec

- [x] 2 realm + 403
- [x] Tách common-auth \| admin \| **org** (đổi tên từ authority)
- [x] Org = chỉ employee READ; bỏ xem dept/job-position
- [x] Home hub grid, không sidebar trên `/home`
- [x] Admin = matrix + sys_user list/lock/reset MK
- [x] Đổi MK bản thân = common-auth
- [x] common-auth + admin docs đã có
- [x] Cập nhật common-auth FR-07 theo hub grid (không sidebar)
- [x] Viết 4 file **org** → `docs/modules/org/` (`employee:READ` only)
- [x] Bảng URL realm SSoT trong common-auth Design; sys-user **không** detail (gộp list)
- [x] Xóa dead-code an toàn: AccountSession, PermissionMatrix* cũ (role), JSP matrix authority
- [ ] Refactor runtime: AuthFilter + PositionPermissionMatrix + UserSession + web.xml 403 + home hub (giữ RolePermissionMatrix tạm đến khi thay)
- [ ] Review DoR rồi AI implement common-auth → admin → org
- [x] Folder docs authority cũ — user đã xóa