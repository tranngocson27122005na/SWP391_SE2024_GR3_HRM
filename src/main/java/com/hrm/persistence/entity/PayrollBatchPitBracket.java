package com.hrm.persistence.entity;

import java.math.BigDecimal;

public class PayrollBatchPitBracket {
    private Integer id;
    private Integer batchId;
    private Integer bracketLevel;
    private BigDecimal lowerBound;
    private BigDecimal upperBound;
    private BigDecimal rate;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public Integer getBracketLevel() { return bracketLevel; }
    public void setBracketLevel(Integer bracketLevel) { this.bracketLevel = bracketLevel; }
    public BigDecimal getLowerBound() { return lowerBound; }
    public void setLowerBound(BigDecimal lowerBound) { this.lowerBound = lowerBound; }
    public BigDecimal getUpperBound() { return upperBound; }
    public void setUpperBound(BigDecimal upperBound) { this.upperBound = upperBound; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
}
