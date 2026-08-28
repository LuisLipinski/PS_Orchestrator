package com.mypetadmin.ps_orchestrator.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionControllerTest {

    @Test
    void retornaServicoEVersaoConfigurada() {
        var controller = new VersionController("abc123");

        var response = controller.version();

        assertThat(response).containsEntry("service", "ps-orchestrator");
        assertThat(response).containsEntry("version", "abc123");
    }
}
