package br.org.edu.ifrn.apiusuarios.repository;

import br.org.edu.ifrn.apiusuarios.model.LogOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogOperacaoRepository extends JpaRepository<LogOperacao, Long> {
}