package med.voll.api.domain.paciente;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAllByAtivoTrue(Pageable paginacao);

    // busca personalizada que retorna somente o atributo ativo, usando como parâmetro o idPaciente
    @Query("select p.ativo from Paciente p where p.id = :id")
    Boolean findAtivoById(Long id);
}
