package com.haroldo.minhasfinancas.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haroldo.minhasfinancas.api.dto.UsuarioDTO;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.service.LancamentoService;
import com.haroldo.minhasfinancas.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {

        //Cenario

        // BACK-END
        String email = "usuario@email.com";
        String senha = "123";

        // Representa o objeto
        Usuario usuarioAutenticado = Usuario.builder()
                .id(1L)
                .nome("Fulano 1")
                .email(email)
                .senha(senha)
                .build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuarioAutenticado);

        // Representa o json
        UsuarioDTO dto = UsuarioDTO.builder()
                .nome("Fulano 1")
                .email(email)
                .senha(senha)
                .build();

        //Converter o dto em json para enviar na requisicao
        String json = new ObjectMapper().writeValueAsString(dto);

        // FRONT-END

        //Execucao e verificacao

        //Para criar uma requisicao do tipo POST
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioAutenticado.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioAutenticado.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioAutenticado.getEmail()));

    }

}
