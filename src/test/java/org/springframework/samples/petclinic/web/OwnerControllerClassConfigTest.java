package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @SpringJUnitWebConfig is a composed annotation that combines @ExtendWith(SpringExtension.class) from JUnit Jupiter
 * with @ContextConfiguration and @WebAppConfiguration from the Spring TestContext Framework.
 * <p>
 * Se ejecuta con el contexto real que especificamos en las locations.
 * <p>
 * Se puede combinar Test que cargan el contexto con test que usan mockito stubs (@ExtendWith(MockitoExtension.class))
 */
@SpringJUnitWebConfig(OwnerControllerClassConfigTest.TestConfig.class)
@ExtendWith(MockitoExtension.class)
class OwnerControllerClassConfigTest {

    @Configuration()
    @ComponentScan(value = {"org.springframework.samples.petclinic.web"})
    static class TestConfig {
        @Bean(value = "clinicService2")
        ClinicService getClinicService() {
            return mock(ClinicService.class);
        }
    }

    @Autowired
    OwnerController ownerController;

    @Autowired
    @Qualifier("clinicService2")
    ClinicService clinicService;

    MockMvc mockMvc;

    /**
     * @Captor para usar esto necesitamos la anotacion @ExtendWith(MockitoExtension.class) en la clase
     */
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    ArgumentCaptor<Owner> ownerArgumentCaptor;

    @BeforeEach
    void setUp() {
        // Mockito.mock(ClinicService.class)
        /**
         * Inicializamos el MVC Mock
         */
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @AfterEach
    void tearDown() {
        /**
         * Tenemos que resetear el Mock de clinicService por que si no fallan
         * Smart Mockito users hardly use this feature because they know it could be a sign of poor tests. Normally,
         * you don't need to reset your mocks, just create new mocks for each test method.
         */
        reset(clinicService);
    }

    @Test
    void testprocessUpdateOwnerForm() throws Exception {
        //doNothing().when(clinicService).saveOwner(ownerArgumentCaptor.capture());
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St ")
                        .param("city", "Key West")
                        .param("telephone", "3151231234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));

        //ArgumentCaptor<Owner> ownerArgumentCaptor = ArgumentCaptor.forClass(Owner.class);
        verify(clinicService).saveOwner(ownerArgumentCaptor.capture());
        //then(clinicService).should().saveOwner(ownerArgumentCaptor.capture());

        assertThat("Jimmy").isEqualTo(ownerArgumentCaptor.getValue().getFirstName());
        assertEquals("Jimmy", ownerArgumentCaptor.getValue().getFirstName());
    }

    @Test
    void testprocessUpdateOwnerFormNotValid() throws Exception {
        /**
         * No se llega a ejecutar el método save ya que hay errores de validación que se validan
         * antes de llamar al método save
         */
        //doNothing().when(clinicService).saveOwner(ownerArgumentCaptor.capture());
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "city"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }


    @Test
    void testNewOwnerPostValid() throws Exception {
        //doNothing().when(clinicService).saveOwner(ownerArgumentCaptor.capture());
        mockMvc.perform(post("/owners/new")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("address", "123 Duval St ")
                        .param("city", "Key West")
                        .param("telephone", "3151231234"))
                .andExpect(status().is3xxRedirection());

        //ArgumentCaptor<Owner> ownerArgumentCaptor = ArgumentCaptor.forClass(Owner.class);
        verify(clinicService).saveOwner(ownerArgumentCaptor.capture());
        //then(clinicService).should().saveOwner(ownerArgumentCaptor.capture());

        assertThat("Jimmy").isEqualTo(ownerArgumentCaptor.getValue().getFirstName());
        assertEquals("Jimmy", ownerArgumentCaptor.getValue().getFirstName());
    }

    @Test
    void testNewOwnerPostNotValid() throws Exception {
        /**
         * No se llega a ejecutar el método save ya que hay errores de validación que se validan
         * antes de llamar al método save
         */
        //doNothing().when(clinicService).saveOwner(ownerArgumentCaptor.capture());
        mockMvc.perform(post("/owners/new")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "city"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
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

//    @Test
//    void tempPOJOTest() throws Exception {
//        String result = controller.initCreationForm(model);
//        System.out.println("result = " + result);
//
//        //then
//        then(clinicService).shouldHaveZeroInteractions();
//        then(model).should().put(anyString(), any());
//        assertThat("owners/createOrUpdateOwnerForm").isEqualToIgnoringCase(result);
//    }
}