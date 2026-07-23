package com.hrm.service.org;

import com.hrm.dto.request.ContractFormRequest;
import com.hrm.dto.request.EmployeeFormRequest;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.Contract;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.JobPosition;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.entity.enums.ContractType;
import com.hrm.persistence.entity.enums.EmploymentGroup;
import com.hrm.persistence.entity.enums.Gender;
import com.hrm.persistence.entity.enums.SalaryType;
import com.hrm.persistence.mapper.ContractMapper;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.persistence.mapper.JobPositionMapper;
import com.hrm.utility.DateFormats;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeeCommandService {

    public int createWithFirstContract(UserSession user, EmployeeFormRequest empReq, ContractFormRequest conReq) {
        assertHrs(user);
        validateEmployee(empReq, true);
        validateContract(conReq, false);

        return SqlExecutor.executeTransaction(session -> {
            EmployeeMapper empMapper = session.getMapper(EmployeeMapper.class);
            ContractMapper conMapper = session.getMapper(ContractMapper.class);
            JobPositionMapper jpMapper = session.getMapper(JobPositionMapper.class);

            if (empMapper.selectByCode(empReq.getEmployeeCode().trim()) != null) {
                throw new BusinessException("Mã nhân viên đã tồn tại");
            }
            JobPosition jp = jpMapper.selectByPrimaryKey(empReq.getPositionId().intValue());
            if (jp == null || jp.getStatus() == null || jp.getStatus() != ActiveStatus.ACTIVE.toByte()) {
                throw new ValidationException("Chức vụ không hợp lệ");
            }

            Employee emp = toEmployee(empReq);
            emp.setCurrentContractId(null);
            emp.setStatus(ActiveStatus.ACTIVE.toByte());
            empMapper.insert(emp);

            Contract con = toContract(conReq, emp.getEmployeeId());
            con.setStatus(ActiveStatus.ACTIVE.toByte());
            conMapper.insert(con);

            empMapper.updateCurrentContractId(emp.getEmployeeId(), con.getContractId());
            return emp.getEmployeeId();
        });
    }

    public void updateProfile(UserSession user, Integer employeeId, EmployeeFormRequest empReq) {
        assertHrs(user);
        if (employeeId == null) {
            throw new ValidationException("Nhân viên không hợp lệ");
        }
        validateEmployee(empReq, false);

        SqlExecutor.execute(EmployeeMapper.class, m -> {
            Employee existing = m.selectByPrimaryKey(employeeId);
            if (existing == null) {
                throw new ValidationException("Nhân viên không tồn tại");
            }
            JobPosition jp = SqlExecutor.execute(JobPositionMapper.class,
                    j -> j.selectByPrimaryKey(empReq.getPositionId().intValue()));
            if (jp == null || jp.getStatus() == null || jp.getStatus() != ActiveStatus.ACTIVE.toByte()) {
                throw new ValidationException("Chức vụ không hợp lệ");
            }
            existing.setFullName(empReq.getFullName().trim());
            existing.setGender((byte) Gender.fromCode(empReq.getGender()).getCode());
            existing.setBirthDate(DateFormats.parseDate(empReq.getBirthDate()));
            existing.setBankAccount(blankToNull(empReq.getBankAccount()));
            existing.setPositionId(empReq.getPositionId().intValue());
            existing.setEmploymentGroup((byte) EmploymentGroup.fromCode(empReq.getEmploymentGroup()).getCode());
            existing.setJoiningDate(DateFormats.parseDate(empReq.getJoiningDate()));
            // không đổi mã NV / current_contract_id
            return m.updateByPrimaryKey(existing);
        });
    }

    private void assertHrs(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Chỉ USER được thao tác hồ sơ");
        }
    }

    private void validateEmployee(EmployeeFormRequest req, boolean requireCode) {
        if (req == null) {
            throw new ValidationException("Thiếu dữ liệu nhân viên");
        }
        if (requireCode && (req.getEmployeeCode() == null || req.getEmployeeCode().isBlank())) {
            throw new ValidationException("Mã nhân viên không hợp lệ");
        }
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new ValidationException("Họ tên không được để trống");
        }
        if (Gender.fromCode(req.getGender()) == null) {
            throw new ValidationException("Giới tính không hợp lệ");
        }
        if (EmploymentGroup.fromCode(req.getEmploymentGroup()) == null) {
            throw new ValidationException("Nhóm lao động không hợp lệ");
        }
        if (req.getPositionId() == null) {
            throw new ValidationException("Chức vụ không hợp lệ");
        }
        if (DateFormats.parseDate(req.getJoiningDate()) == null) {
            throw new ValidationException("Ngày vào không hợp lệ");
        }
    }

    private void validateContract(ContractFormRequest req, boolean requireEmployeeId) {
        if (req == null) {
            throw new ValidationException("Thiếu dữ liệu hợp đồng");
        }
        if (requireEmployeeId && req.getEmployeeId() == null) {
            throw new ValidationException("Nhân viên không hợp lệ");
        }
        if (ContractType.fromCode(req.getContractType()) == null) {
            throw new ValidationException("Loại hợp đồng không hợp lệ");
        }
        if (SalaryType.fromCode(req.getSalaryType()) == null) {
            throw new ValidationException("Hình thức lương không hợp lệ");
        }
        if (DateFormats.parseDate(req.getStartDate()) == null) {
            throw new ValidationException("Ngày bắt đầu không hợp lệ");
        }
        try {
            BigDecimal sal = new BigDecimal(req.getBasicSalary() == null ? "" : req.getBasicSalary().trim());
            if (sal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Mức lương không hợp lệ");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Mức lương không hợp lệ");
        }
    }

    private Employee toEmployee(EmployeeFormRequest req) {
        Employee emp = new Employee();
        emp.setEmployeeCode(req.getEmployeeCode().trim());
        emp.setFullName(req.getFullName().trim());
        emp.setGender((byte) Gender.fromCode(req.getGender()).getCode());
        emp.setBirthDate(DateFormats.parseDate(req.getBirthDate()));
        emp.setBankAccount(blankToNull(req.getBankAccount()));
        emp.setPositionId(req.getPositionId().intValue());
        emp.setEmploymentGroup((byte) EmploymentGroup.fromCode(req.getEmploymentGroup()).getCode());
        emp.setJoiningDate(DateFormats.parseDate(req.getJoiningDate()));
        return emp;
    }

    private Contract toContract(ContractFormRequest req, Integer employeeId) {
        Contract c = new Contract();
        c.setEmployeeId(employeeId);
        c.setContractType((byte) ContractType.fromCode(req.getContractType()).getCode());
        c.setStartDate(DateFormats.parseDate(req.getStartDate()));
        c.setEndDate(DateFormats.parseDate(req.getEndDate()));
        c.setBasicSalary(new BigDecimal(req.getBasicSalary().trim()));
        c.setSalaryType((byte) SalaryType.fromCode(req.getSalaryType()).getCode());
        return c;
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
