package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.service.ClinicService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @InjectMocks
    OwnerController controller;

    @Mock
    ClinicService clinicService;

    @Mock
    Map<String, Object> model;

    @Test
    void tempPOJOTest() throws Exception {
        String result = controller.initCreationForm(model);
        System.out.println("result = " + result);

        //then
        then(clinicService).shouldHaveZeroInteractions();
        then(model).should().put(anyString(), any());
        assertThat("owners/createOrUpdateOwnerForm").isEqualToIgnoringCase(result);
    }
}