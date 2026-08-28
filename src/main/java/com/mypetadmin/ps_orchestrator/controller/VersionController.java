package com.mypetadmin.ps_orchestrator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class VersionController {

    private final String version;

    public VersionController(@Value("${app.version:dev}") String version) {
        this.version = version;
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("service", "ps-orchestrator", "version", version);
    }
}
