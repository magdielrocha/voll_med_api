package med.voll.api.domain.consulta;

import jakarta.validation.Valid;
import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsultas;
import med.voll.api.domain.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultas {

    @Autowired
    private ConsultaRepository consultaRepository;

    // injetando os repositoryes medico e paciente porque precisamos passar o objeto completo e não somente o id
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    // para evitar ter que declarar todos os validadores, o Spring permite declarar um atributo List com um generics do tipo da interface que
    @Autowired
    private List<ValidadorAgendamentoDeConsultas> validadores;

    @Autowired
    private List<ValidadorCancelamentoDeConsultas> validadoresCancelamento;

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {

        // Verifica se o id do paciente e do médico existe
        if(!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ValidacaoException("Id do paciente informado não existe!");
        }
        // o id do paciente precisa existir, ou seja diferente de null, para depois verificar se o id existe no banco
        if(dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
            throw new ValidacaoException("Id do médico informado não existe!");
        }
        // Design Pattern > Strategy
        // é mais ou menos, porque usa mais de uma estratégia
        // Aqui estamos usando 3 letras do SOLID
        // SOD
        validadores.forEach(v -> v.validar(dados));

        // Antes desse trecho precisamos passar as regras de negócio

        // instanciando a variável pegando o id que está vindo do parâmetro de agendar().
        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        // a regra de negócio diz que a escolha do médico é opcional, por isso ele pode vir null
        // para não dar erro criamos um metodo privado chamado "escolherMedico" que escolhe um médico de forma randômica
        var medico = escolherMedico(dados);

        // se não houver nenhum médico disponível
        if (medico == null) {
            throw new ValidacaoException("Não existe médico disponível nessa data!");
        }
        // passando null, porque o id quem vai criar é o banco de dados
        // dados.data() vindos do parêmetro de agendar().
        var consulta = new Consulta(null, medico, paciente, dados.data(), null);
        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);

    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {

        // se o usuário escolher um médico, carrega o id do médico do banco de dados
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }
        // se o usuário não escolher um médico, precisa escolher a especialidade
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando médico não for escolhido!");
        }
        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
    }

    public void cancelar(@Valid DadosCancelamentoConsulta dados) {
        if(!consultaRepository.existsById(dados.idConsulta())) {
            throw new ValidacaoException("Id da consulta informado não existe!");
        }

        validadoresCancelamento.forEach(v -> v.validar(dados));

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());
        consulta.cancelar(dados.movito());
    }
}
