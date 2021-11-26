package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Mock MVC Standalone Test
 */
@ExtendWith(MockitoExtension.class)
class VetControllerMockMVCStandaloneTest {

    @Mock
    ClinicService clinicService;

    @InjectMocks
    VetController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        List<Vet> vetsList = new ArrayList<>();
        vetsList.add(new Vet());
        System.out.println("vetsList = " + vetsList);
        given(clinicService.findVets()).willReturn(vetsList);

        /**
         * Inicializamos el MVC Mock
         */
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * MVC Test
     */
    @Test
    void testControllerMVCMockToShowVetList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/vets.html"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vets"))
                .andExpect(view().name("vets/vetList"))
                .andReturn();

        mvcResult.getModelAndView().getModel().forEach(((s, o) -> {
            System.out.println(s + " = " + o);
        }));

        System.out.println("mvcResult.getModelAndView().getModel() = " + mvcResult.getModelAndView().getModel());
        System.out.println("mvcResult.getModelAndView().getViewName() = " + mvcResult.getModelAndView().getViewName());
        assertThat(mvcResult.getModelAndView().getModel().containsKey("vets")).isTrue();
        assertThat(mvcResult.getModelAndView().getModel().keySet()).contains("vets");
        assertThat(mvcResult.getModelAndView().getViewName()).isEqualTo("vets/vetList");

    }
}