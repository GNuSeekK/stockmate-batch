package com.stockmate.batch.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NamingUtil {

    @Value("${spring.profiles.active}")
    private String profile;

}
