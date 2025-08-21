package med.voll.api.domain.consulta.validacoes.agendamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioDeFundionamentoClinica implements ValidadorAgendamentoDeConsultas {

    public void validar(DadosAgendamentoConsulta dados) {

        var dataConsulta = dados.data();
        var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var antesDaAberturaDaClicina = dataConsulta.getHour() < 7;
        var depoisDoEncerramentoClicina = dataConsulta.getHour() > 18;

        if (domingo || antesDaAberturaDaClicina || depoisDoEncerramentoClicina) {
            throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica");
        }
    }

}
