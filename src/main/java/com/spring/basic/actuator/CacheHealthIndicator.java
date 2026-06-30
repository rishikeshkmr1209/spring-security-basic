package com.spring.basic.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class CacheHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isCacheUp = checkCacheStatus();
        return isCacheUp ? Health.up().withDetail("Cache","Available").build(): 
        Health.down().withDetail("Cache", "Not-Available").build();
    }
     private boolean checkCacheStatus(){
        return false;
    }
}