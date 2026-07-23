package com.hrm.persistence.mapper;

import com.hrm.persistence.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceMapper {
    int deleteByPrimaryKey(Integer balanceId);

    int insert(LeaveBalance row);

    LeaveBalance selectByPrimaryKey(Integer balanceId);

    List<LeaveBalance> selectAll();

    int updateByPrimaryKey(LeaveBalance row);
}