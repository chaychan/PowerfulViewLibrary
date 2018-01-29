package com.chaychan.viewlib;


import android.animation.TypeEvaluator;

import java.math.BigDecimal;

class BigDecimalEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        BigDecimal start = (BigDecimal) startValue;
        BigDecimal end = (BigDecimal) endValue;
        BigDecimal result = end.subtract(start);
        return result.multiply(new BigDecimal("" + fraction)).add(start);
    }
}