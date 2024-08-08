package com.stockmate.batch.quant.helper;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test | crawling")
public class TestHelperImpl implements TestHelper {

    @Override
    public void doTest() {

    }
}
