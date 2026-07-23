package com.github.dingdaoyi.rule.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputScheduleConfigTest {

    @Test
    void cronFieldRoundTrips() {
        InputScheduleConfig cfg = new InputScheduleConfig();
        cfg.setCron("0 0 23 * * ?");
        cfg.setDeviceKey("device-001");
        cfg.setTimezone("Asia/Shanghai");

        assertThat(cfg.getCron()).isEqualTo("0 0 23 * * ?");
        assertThat(cfg.getDeviceKey()).isEqualTo("device-001");
        assertThat(cfg.getTimezone()).isEqualTo("Asia/Shanghai");
    }

    @Test
    void defaultsAreNull() {
        InputScheduleConfig cfg = new InputScheduleConfig();
        assertThat(cfg.getCron()).isNull();
        assertThat(cfg.getDeviceKey()).isNull();
        assertThat(cfg.getTimezone()).isNull();
    }
}
