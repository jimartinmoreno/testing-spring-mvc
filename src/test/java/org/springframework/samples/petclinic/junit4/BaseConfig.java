package org.springframework.samples.petclinic.junit4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.sfg.HearingInterpreter;
import org.springframework.samples.petclinic.sfg.WordProducer;

/**
 * Created by jt on 2019-02-16.
 */
@Configuration
public class BaseConfig {
    @Bean
    HearingInterpreter hearingInterpreter(WordProducer wordProducer) {
        return new HearingInterpreter(wordProducer);
    }
}
