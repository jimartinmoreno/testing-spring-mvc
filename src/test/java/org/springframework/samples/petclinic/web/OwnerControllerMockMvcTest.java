package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class OwnerControllerMockMvcTest {

    @InjectMocks
    OwnerController ownerController;

    @Mock
    ClinicService clinicService;

    MockMvc mockMvc;

    /**
     * @Captor para usar esto necesitamos la anotacion @ExtendWith(MockitoExtension.class) en la clase
     */
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    /******************************/

    @BeforeEach
    void setUp() {
        /**
         * Inicializamos el MVC Mock
         */
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }


    @AfterEach
    void tearDown() {
        /**
         * Tenemos que resetear el Mock de clinicService por que si no fallan
         */
        //reset(clinicService);
    }

    @Test
    void testReturnListOfOwners() throws Exception {
        given(clinicService.findOwnerByLastName(anyString())).willReturn(Lists.newArrayList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andDo(print()) // Pinta la peticion
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));

        // Indicamos un argument captor para validar el argumento que recibe findOwnerByLastName para validar que
        // si no le enviamos nada le mete un blank
        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());
        //verify(clinicService).findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isBlank();

    }

    @Test
    void testReturnJustOneOwner() throws Exception {
        Owner owner = new Owner();
        List<Owner> ownerList = List.of(owner);
        given(clinicService.findOwnerByLastName(anyString())).willReturn(ownerList);

        mockMvc.perform(get("/owners")
                        .param("lastName", "Martin"))
                .andDo(print()) // Pinta la peticion
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + owner.getId()));

        // Indicamos un argument captor para validar el argumento que recibe findOwnerByLastName para validar que
        // si no le enviamos nada le mete un blank
        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());
        //verify(clinicService).findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualTo("Martin");

    }

    @Test
    void testFindByNameNotFound() throws Exception {
        List<Owner> ownerList = Lists.emptyList();
        given(clinicService.findOwnerByLastName(anyString())).willReturn(ownerList);
        mockMvc.perform(get("/owners")
                        .param("lastName", "Dont find ME!")
                        .param("firstName", "Nacho"))
                .andDo(print()) // Pinta la peticion
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));

        // Indicamos un argument captor para validar el argumento que recibe findOwnerByLastName
        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("Dont find ME!");
    }

    @Test
    void initCreationFormTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"))
                .andReturn();

        mvcResult.getModelAndView().getModel().forEach(((s, o) -> {
            System.out.println(s + " = " + o);
        }));

        System.out.println("mvcResult.getModelAndView().getModel() = " + mvcResult.getModelAndView().getModel());
        System.out.println("mvcResult.getModelAndView().getViewName() = " + mvcResult.getModelAndView().getViewName());
        assertThat(mvcResult.getModelAndView().getModel().containsKey("owner")).isTrue();
        assertThat(mvcResult.getModelAndView().getModel().keySet()).contains("owner");
        assertThat(mvcResult.getModelAndView().getViewName()).isEqualTo("owners/createOrUpdateOwnerForm");
    }
}