package com.stockmate.batch.controller;

import com.stockmate.batch.fmp.service.FmpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final FmpService fmpService;

    @GetMapping("/save")
    public void save() {
        fmpService.saveSymbolInfo();
    }

}
