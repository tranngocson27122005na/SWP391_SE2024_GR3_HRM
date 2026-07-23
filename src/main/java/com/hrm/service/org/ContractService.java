package com.hrm.service.org;

import com.hrm.dto.request.ContractFormRequest;
import com.hrm.dto.response.ContractResponse;
import com.hrm.dto.response.EmployeeResponse;
import com.hrm.dto.session.UserSession;
import com.hrm.infrastructure.exception.BusinessException;
import com.hrm.infrastructure.exception.UnauthorizedException;
import com.hrm.infrastructure.exception.ValidationException;
import com.hrm.infrastructure.persistence.executor.SqlExecutor;
import com.hrm.persistence.entity.Contract;
import com.hrm.persistence.entity.Employee;
import com.hrm.persistence.entity.enums.ActiveStatus;
import com.hrm.persistence.entity.enums.ContractType;
import com.hrm.persistence.entity.enums.SalaryType;
import com.hrm.persistence.mapper.ContractMapper;
import com.hrm.persistence.mapper.EmployeeMapper;
import com.hrm.utility.DateFormats;

import java.math.BigDecimal;
import java.util.List;

public class ContractService {

    private final EmployeeQueryService employeeQuery = new EmployeeQueryService();

    public List<ContractResponse> listByEmployee(UserSession user, Integer employeeId) {
        assertCanReadEmployee(user, employeeId);
        Employee emp = SqlExecutor.execute(EmployeeMapper.class, m -> m.selectByPrimaryKey(employeeId));
        List<ContractResponse> rows = SqlExecutor.execute(ContractMapper.class, m -> m.selectByEmployeeId(employeeId));
        if (rows == null) {
            return List.of();
        }
        Integer currentId = emp == null ? null : emp.getCurrentContractId();
        for (ContractResponse row : rows) {
            enrich(row, currentId);
        }
        return rows;
    }

    public ContractResponse getDetail(UserSession user, Integer contractId) {
        Contract c = SqlExecutor.execute(ContractMapper.class, m -> m.selectByPrimaryKey(contractId));
        if (c == null) {
            throw new ValidationException("Hợp đồng không hợp lệ");
        }
        assertCanReadEmployee(user, c.getEmployeeId());
        Employee emp = SqlExecutor.execute(EmployeeMapper.class, m -> m.selectByPrimaryKey(c.getEmployeeId()));
        ContractResponse row = toResponse(c);
        enrich(row, emp == null ? null : emp.getCurrentContractId());
        return row;
    }

    public void createOrSwitch(UserSession user, ContractFormRequest req) {
        assertUser(user);
        if (req == null || req.getEmployeeId() == null) {
            throw new ValidationException("Nhân viên không hợp lệ");
        }
        assertCanReadEmployee(user, req.getEmployeeId().intValue());
        validate(req);

        SqlExecutor.executeTransaction(session -> {
            ContractMapper cm = session.getMapper(ContractMapper.class);
            EmployeeMapper em = session.getMapper(EmployeeMapper.class);
            Integer empId = req.getEmployeeId().intValue();
            Employee emp = em.selectByPrimaryKey(empId);
            if (emp == null) {
                throw new ValidationException("Nhân viên không hợp lệ");
            }
            // First contract (current null) hoặc switch: inactive ACTIVE cũ rồi insert ACTIVE mới
            cm.inactiveAllActiveByEmployee(empId);

            Contract c = new Contract();
            c.setEmployeeId(empId);
            c.setContractType((byte) ContractType.fromCode(req.getContractType()).getCode());
            c.setStartDate(DateFormats.parseDate(req.getStartDate()));
            c.setEndDate(DateFormats.parseDate(req.getEndDate()));
            c.setBasicSalary(new BigDecimal(req.getBasicSalary().trim()));
            c.setSalaryType((byte) SalaryType.fromCode(req.getSalaryType()).getCode());
            c.setStatus(ActiveStatus.ACTIVE.toByte());
            cm.insert(c);
            em.updateCurrentContractId(empId, c.getContractId());
            return c.getContractId();
        });
    }

    public void softDelete(UserSession user, Integer contractId) {
        assertUser(user);
        Contract c = SqlExecutor.execute(ContractMapper.class, m -> m.selectByPrimaryKey(contractId));
        if (c == null) {
            throw new ValidationException("Hợp đồng không hợp lệ");
        }
        assertCanReadEmployee(user, c.getEmployeeId());

        SqlExecutor.executeTransaction(session -> {
            ContractMapper cm = session.getMapper(ContractMapper.class);
            EmployeeMapper em = session.getMapper(EmployeeMapper.class);
            cm.updateStatus(contractId, ActiveStatus.INACTIVE.toByte());
            Employee emp = em.selectByPrimaryKey(c.getEmployeeId());
            if (emp != null && contractId.equals(emp.getCurrentContractId())) {
                em.updateCurrentContractId(c.getEmployeeId(), null);
            }
            return true;
        });
    }

    public void activate(UserSession user, Integer contractId) {
        assertUser(user);
        Contract c = SqlExecutor.execute(ContractMapper.class, m -> m.selectByPrimaryKey(contractId));
        if (c == null) {
            throw new ValidationException("Hợp đồng không hợp lệ");
        }
        assertCanReadEmployee(user, c.getEmployeeId());

        SqlExecutor.executeTransaction(session -> {
            ContractMapper cm = session.getMapper(ContractMapper.class);
            EmployeeMapper em = session.getMapper(EmployeeMapper.class);
            cm.inactiveAllActiveByEmployee(c.getEmployeeId());
            cm.updateStatus(contractId, ActiveStatus.ACTIVE.toByte());
            em.updateCurrentContractId(c.getEmployeeId(), contractId);
            return true;
        });
    }

    public void updateFields(UserSession user, Integer contractId, ContractFormRequest req) {
        assertUser(user);
        Contract c = SqlExecutor.execute(ContractMapper.class, m -> m.selectByPrimaryKey(contractId));
        if (c == null) {
            throw new ValidationException("Hợp đồng không hợp lệ");
        }
        assertCanReadEmployee(user, c.getEmployeeId());
        validate(req);
        c.setContractType((byte) ContractType.fromCode(req.getContractType()).getCode());
        c.setStartDate(DateFormats.parseDate(req.getStartDate()));
        c.setEndDate(DateFormats.parseDate(req.getEndDate()));
        c.setBasicSalary(new BigDecimal(req.getBasicSalary().trim()));
        c.setSalaryType((byte) SalaryType.fromCode(req.getSalaryType()).getCode());
        SqlExecutor.execute(ContractMapper.class, m -> m.updateByPrimaryKey(c));
    }

    private void assertCanReadEmployee(UserSession user, Integer employeeId) {
        // Dùng query detail theo scope — nếu không được xem emp thì không xem HĐ
        employeeQuery.getDetail(user, employeeId.longValue());
    }

    private void assertUser(UserSession user) {
        if (user == null || !user.isUser()) {
            throw new UnauthorizedException("Chỉ USER được thao tác hợp đồng");
        }
    }

    private void validate(ContractFormRequest req) {
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

    private void enrich(ContractResponse row, Integer currentId) {
        row.setContractTypeLabel(ContractType.labelOf(row.getContractType()));
        row.setSalaryTypeLabel(SalaryType.labelOf(row.getSalaryType()));
        row.setCurrent(currentId != null && currentId.equals(row.getContractId()));
    }

    private ContractResponse toResponse(Contract c) {
        ContractResponse row = new ContractResponse();
        row.setContractId(c.getContractId());
        row.setEmployeeId(c.getEmployeeId());
        row.setContractType(c.getContractType() == null ? null : c.getContractType().intValue());
        row.setStartDate(c.getStartDate());
        row.setEndDate(c.getEndDate());
        row.setBasicSalary(c.getBasicSalary());
        row.setSalaryType(c.getSalaryType() == null ? null : c.getSalaryType().intValue());
        row.setStatus(c.getStatus() == null ? null : c.getStatus().intValue());
        row.setCreatedAt(c.getCreatedAt());
        return row;
    }
}
