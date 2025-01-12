package com.econet.model;

import java.math.BigDecimal;

public class NumberToDollarsRequest {
    private BigDecimal dNum;

    public BigDecimal getNumberToDollar() {
        return dNum;
    }

    public void setNumberToDollar(BigDecimal dNum) {
        this.dNum = dNum;
    }
}
