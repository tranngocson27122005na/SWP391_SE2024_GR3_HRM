## METADATA

### CONFIG\_METADATA: 

* Artifact\_ID: HRMS-L1-COMP-POL-001 .  
* Version: 1.2.0   
* Document\_Status: Locked   
* Document\_Title: TÀI LIỆU YÊU CẦU CHỨC NĂNG PHẦN MỀM HRMS (CORE MODULES)   
* Scope\_Context: Generic HRMS Core Modules     
* Source\_Format: Markdown    
* Precedence\_Level: Primary    
* Dependency\_List: \[**LAW-VN-001**\] 

### GOVERNANCE\_TOGGLES: 

* Enforce\_RFC2119\_Strictness: Yes  
* Ignore\_Prose\_Ambiguity: Yes  
* Enforce\_Raw\_Extraction: Yes  
* Eliminate\_AI\_Tone: Yes 

### KNOWLEDGE\_BOUNDARY: 

* Data\_Lock\_Internal\_Only: Yes  
* Strict\_Zero\_Inference: Yes  
* Permit\_External\_Glossary: Yes  
* Zero\_Assumption\_Policy: Yes  

### DATA\_INTEGRITY: 

* Ban\_Semantic\_Compression: Yes  
* Ban\_Paraphrasing\_Legal\_Terms: Yes  
* Halt\_On\_Conflict: Yes  
* Conflict\_Priority\_Level: High  
* System\_Level\_Linkage: OpenAPI


### FALLBACK\_SETTINGS: 

* Fallback\_Trigger: Out\_Of\_Bounds  
* Fallback\_Exact\_String: "Không tìm thấy dữ liệu trong tài liệu hiện hành."  
* Mute\_Speculative\_Advice: Yes

### TRACEABILITY\_SETTINGS: 

* Hierarchy ID: SYS-HRMS-CORE-001  
* Mandatory\_Citation: Yes  
* Citation\_Format: Inline  
* Citation\_Placement: Immediate\_After\_Clause 

## 

## AI EXECUTION POLICY

### 1\. Normative Language (Ngôn ngữ định chuẩn)

* **Applicable\_Standards:** RFC2119, RFC8174  **.**  
* **Hard\_Coded\_Rules:**  MUST, MUST\_NOT, SHOULD, MAY  

### 2\. Knowledge Boundary (Ranh giới kiến thức)

* **Data Lock:** Yes .  
* **Internal\_Only:** Yes .  
* **Ban\_Semantic\_Compression**:Yes .

### 3\. Data Integrity (Tính toàn vẹn dữ liệu)

* **No\_Modification:** Yes .  
* **Source\_Preservation:** Yes .  
* **System\_Level\_Linkage:** Yes 

### 4\. Hallucination Control (Kiểm soát suy diễn)

* **Zero\_Inference:** {{EXECUTION.Zero\_Inference}}.  
* **Conflict\_Reporting:** {{EXECUTION.Conflict\_Reporting}}.  
* **Zero\_Assumption\_Policy**: {{EXECUTION.Zero\_Assumption\_Policy}}

### 5\. Fall-back Policy (Chính sách dự phòng)

* **Out\_Of\_Scope\_Handling:**  "Không tìm thấy dữ liệu trong tài liệu hiện hành."   
* **Mute\_Speculative\_Advice:** Yes .

### 6\. Output Format (Định dạng đầu ra)

* **Extraction\_Mode:**  Raw\_Data   
* **Eliminate\_AI\_Tone:** Yes 

### 7\. Traceability & Validation (Truy xuất nguồn gốc & Thẩm định)

* **Mandatory\_Citation:** Yes.

* **Traceability\_Tags: \[business-rule, core, release-1\]**  
* **Citation\_Placement**  Immediate\_After\_Clause.

* Change\_Log: v1.2.0 2026-07-01 Author: Locked for CoreMVP scoping

## **TÀI LIỆU YÊU CẦU CHỨC NĂNG PHẦN MỀM HRMS (CORE MODULES)**

*(Business Rule Definition)*

### **1\. Phân hệ Quản lý Hồ sơ & Hợp đồng (Employee & Contract Management)**

**Quản lý Hồ sơ nhân viên:** Lưu trữ thông tin định danh, tài khoản ngân hàng, thông tin thuế.

**Phân nhóm nhân sự:** Phân loại theo khối `Văn phòng` hoặc `Sản xuất` để làm gốc áp dụng các quy tắc chấm công và tính lương 

**Quản lý Hồ sơ người phụ thuộc:** Lưu trữ thông tin người phụ thuộc của nhân viên (họ tên, quan hệ, mã số thuế, thời gian đăng ký)

**Quản lý Hợp đồng lao động (HĐLĐ):**

* Phân loại HĐLĐ: Thử việc, Có thời hạn, Vô thời hạn.  
* Quản lý trạng thái hợp đồng: Hiệu lực, Hết hạn, Đã chấm dứt.  
* Xác định nhân viên đang trong giai đoạn thử việc hay chính thức — làm căn cứ áp dụng mức lương thử việc khi tính lương.  
* Tự động gửi cảnh báo (Notification) khi hợp đồng thử việc hoặc hợp đồng có thời hạn sắp hết hạn

#### **1.4 Onboarding/ Offboarding cơ bản:** 

Tính năng chuyển trạng thái nhân sự (Đang làm việc \-\> Nghỉ việc) để chốt công nợ và đóng quyền truy cập.

### **2\. Phân hệ Lịch làm việc & Chấm công (Time & Attendance)**

**Quản lý Lịch làm (Shifts & Schedules):**

* Xác định ngày công chuẩn trong tháng làm căn cứ tính lương và quy đổi đơn giá giờ.  
* Chỉ áp dụng một loại ca: ca hành chính.  
* Ngày làm việc trong tuần: Thứ Hai – Thứ Sáu.  
* Riêng khối sản xuất: có thể bố trí làm việc thêm ngày Thứ Bảy theo lịch của bộ phận.

**Theo dõi Chấm công:**

* Ghi nhận giờ vào/ra hằng ngày từ dữ liệu nhập ngoài hệ thống (Excel).  
* Tính số công thực tế trong tháng làm căn cứ tính lương, đặc biệt đối với khối sản xuất (nơi lương tính theo giờ công thực tế thay vì lương khoán cố định).

**Quản lý Nghỉ phép:** Chỉ áp dụng hai loại: Nghỉ phép năm (Annual Leave) và Nghỉ không lương (Unpaid Leave). Số ngày phép năm áp dụng cho từng nhân viên được xác định tại Phân hệ Chế độ đãi ngộ (Mục 4).

**Khấu trừ do không đủ giờ công (đi muộn/về sớm):** Đây là cơ chế tính lương theo giờ công thực tế, áp dụng cho cả hai khối lao động — không phải hình thức xử lý kỷ luật. Thời gian vi phạm được quy đổi theo đơn vị chuẩn hóa (block) để tính số tiền khấu trừ tương ứng.

### **3\. Phân hệ Quản lý Tăng ca (Overtime \- OT)**

**Điều kiện phát sinh OT theo khối:**

* Khối văn phòng: không phát sinh OT ngày thường trong giờ hành chính.  
* Khối sản xuất: được phát sinh OT ngày thường, phân theo hai mức số giờ tăng ca.  
* Cả hai khối: được phát sinh OT vào ngày nghỉ hằng tuần và ngày Lễ/Tết.

**Phân loại và hệ số OT:** Ngày thường 150%, Nghỉ hàng tuần 200%, Lễ/Tết 300% 

**Hạn mức chặn:** Giới hạn số giờ OT tối đa theo ngày và theo tháng, nhằm tuân thủ nguyên tắc bảo vệ sức khỏe người lao động.

#### **Ghi chú xử lý Bonus Holiday**  *Không tồn tại entity "Thưởng lễ Tết" riêng*

### **4\. Phân hệ chế độ đãi ngộ** 

* **Phép năm:**Văn phòng 12 ngày, Sản xuất 14 ngày (cơ sở) .

* **Thâm niên phép:** Cộng thêm 1 ngày phép sau mỗi 5 năm làm việc.

* **Phụ cấp:** Bao gồm Phụ cấp trách nhiệm, chức vụ, ăn ca 

* **Thưởng chuyên cần**: 2.000.000 VNĐ/năm, điều kiện không nghỉ phép trong năm 

### **5\. Phân hệ Tiền lương, Phụ cấp & Thuế**

#### *5.1 Earning Elements*  

* Lương cơ bản: theo mức thỏa thuận trong hợp đồng lao động.   
* Lương thử việc: áp dụng thay cho lương cơ bản trong giai đoạn thử việc, hai khoản này loại trừ nhau tùy trạng thái hợp đồng   
* Lương OT: tính theo quy tắc tại Phân hệ 3\.   
* Phụ cấp Chức vụ: khoản phụ cấp cố định theo chức danh đảm nhiệm   
* Phụ cấp Thâm niên:khoản tiền cộng thêm theo thâm niên làm việc — là chế độ **thu nhập bằng tiền**, tách biệt với Phép thâm niên ở Phân hệ 4   
* Phụ cấp ăn ca (có thuế): khoản hỗ trợ chi phí ăn uống trong ca làm việc; cần phân biệt phần vượt ngưỡng quy định vì có cách xử lý thuế khác với phần trong ngưỡng 

#### *5.2 Statutory Elements*

tính trên cơ sở thu nhập chịu bảo hiểm/chịu thuế của nhân viên, có xét đến số người phụ thuộc đã đăng ký (Phân hệ 1\) 

* BHXH, BHYT, BHTN theo tỷ lệ pháp luật quy định.    
* Thuế TNCN chia theo biểu lũy tiến từng phần, áp dụng giảm trừ gia cảnh. 

#### *5.3 Internal Deduction Elements* 

Khấu trừ thiếu giờ công 

####  *5.4 Net Pay* 

Là kết quả tổng hợp giữa Thu nhập, Khấu trừ bắt buộc và Khấu trừ nội bộ 

#### ***5.5 Bảng lương & Phiếu lương*** 

nhân viên xem chi tiết ở Payroll 

### 

### **6\. Quy trình Phê duyệt Tổng quát (Generic Workflows)**

| Loại đơn | Mô tả | Người khởi tạo | Duyệt cấp 1 | Duyệt cấp 2 |
| :---: | :---: | :---: | :---: | :---: |
| Attendance Request | Xin điều chỉnh công | NV / Quản lý | Trưởng bộ phận | HRS |
| Resignation Request | Xin nghỉ việc | NV / Quản lý | HRS | – |

