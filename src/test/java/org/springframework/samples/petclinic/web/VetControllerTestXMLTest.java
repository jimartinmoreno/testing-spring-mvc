package org.springframework.samples.petclinic.web;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @SpringJUnitWebConfig is a composed annotation that combines @ExtendWith(SpringExtension.class) from JUnit Jupiter
 * with @ContextConfiguration and @WebAppConfiguration from the Spring TestContext Framework.
 */

@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class VetControllerTestXMLTest {

    //@Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ClinicService clinicService;

    @Autowired
    VetController vetController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
    //void setUp() {
        this.webApplicationContext = webApplicationContext;

        List<Vet> vetsList = new ArrayList<>();
        vetsList.add(new Vet());
        Vets vets = new Vets();
        vets.getVetList().addAll(vetsList);

        given(clinicService.findVets()).willReturn(vetsList);
        System.out.println("webApplicationContext = " + webApplicationContext);
        // webAppContextSetup
        //this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        //standaloneSetup
        this.mockMvc = MockMvcBuilders.standaloneSetup(vetController).build();
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(webApplicationContext.getBean("vetController"));
    }

    @Test
    void showVetList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/vets.html"))
                .andDo(print()) // Pinta la peticion
                .andExpect(status().isOk())
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(view().name("vets/vetList")).andReturn();

        mvcResult.getModelAndView().getModel().forEach(((s, o) -> {
            System.out.println(s + " = " + o);
        }));
    }

    @Ignore
    @Test
    void showResourcesVetList() throws Exception {

        mockMvc.perform(get("/vets.json"))
                .andDo(print()); // Pinta la peticion
        //.andExpect(status().isOk())
        //.andExpect(content().contentType("application/json;charset=UTF-8"));
        //then
        then(clinicService).should().findVets();

    }
}