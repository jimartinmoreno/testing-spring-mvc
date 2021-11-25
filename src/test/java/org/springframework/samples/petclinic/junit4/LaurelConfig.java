package org.springframework.samples.petclinic.junit4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.sfg.LaurelWordProducer;

/**
 * Created by jt on 2019-02-16.
 */
@Configuration
public class LaurelConfig {
    @Bean
    LaurelWordProducer laurelWordProducer() {
        return new LaurelWordProducer();
    }
}
