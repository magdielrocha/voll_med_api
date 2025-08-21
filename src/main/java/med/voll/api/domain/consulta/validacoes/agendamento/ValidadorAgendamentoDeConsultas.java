package med.voll.api.domain.consulta.validacoes.agendamento;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;

public interface ValidadorAgendamentoDeConsultas {
    //não precisa do public porque é implícito que todos os métodos de interface são públicos
    void validar(DadosAgendamentoConsulta dados);
}
