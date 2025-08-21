package med.voll.api.controller;

import med.voll.api.domain.consulta.AgendaDeConsultas;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.DadosDetalhamentoConsulta;
import med.voll.api.domain.medico.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsultaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired // para simular o json de entrada
    private JacksonTester<DadosAgendamentoConsulta> dadosAgendamentoConsultaJson;

    @Autowired // para simular o json de saída
    private JacksonTester<DadosDetalhamentoConsulta> dadosDetalhamentoConsultaJson;

    @MockitoBean
    private AgendaDeConsultas agendaDeConsultas;

    @Test
    @DisplayName("Deveria devolver codigo 404 quando informações estão inválidas")
    @WithMockUser // simula um usuário logado, para ignorar testes de segurança
    void agendarCenario1() throws Exception {

        // Dispara uma requisição para o endereço "consultas" via metodo post sem levar nenhum corpo
        // Pega o response e joga em uma variável
        var response = mvc.perform(post("/consultas"))
                .andReturn().getResponse();

        // Verifica se o status do response é 400, porque nesse cenário o erro deve ser 400
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo 200 quando informações estão válidas")
    @WithMockUser // simula um usuário logado, para ignorar testes de segurança
    void agendarCenario2() throws Exception {
        var data = LocalDateTime.now().plusHours(1);
        var especialidade = Especialidade.CARDIOLOGIA;

        // Quando a agenda (mock) tiver um metodo chamado "agendar" devolva os parâmetros de dadosDetalhamento
        var dadosDetalhamento = new DadosDetalhamentoConsulta(null, 2l, 5l, data);
        when(agendaDeConsultas.agendar(any())).thenReturn(dadosDetalhamento);

        // EM SEGUIDA DISPARA A REQUISIÇÃO

        // Dispara uma requisição para o endereço "consultas" via metodo post sem levar nenhum corpo
        // Pega o response e joga em uma variável
        var response = mvc
                .perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON) // para passar o cabecalho
                        .content(dadosAgendamentoConsultaJson.write(
                                new DadosAgendamentoConsulta(2l, 5l, data, especialidade)
                        ).getJson())
                )
                .andReturn().getResponse();

        // Verifica se o status do response é 200
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var jsonEsperado = dadosDetalhamentoConsultaJson.write(dadosDetalhamento)
                .getJson();

        // Pega a resposta, devolve como String e verifica se é igual ao json que estamos esperando
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);


    }
}