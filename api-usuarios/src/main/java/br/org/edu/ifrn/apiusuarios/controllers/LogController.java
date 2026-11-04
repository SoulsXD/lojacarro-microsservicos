package br.org.edu.ifrn.apiusuarios.controllers;

import br.org.edu.ifrn.apiusuarios.model.LogOperacao;
import br.org.edu.ifrn.apiusuarios.repository.LogOperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LogController {

    @Autowired
    private LogOperacaoRepository logRepository;

    @GetMapping
    public ResponseEntity<?> listarLogsAdm(@RequestHeader("Cargo-Usuario-Logado") String cargo) {
        if (!"ADMINISTRADOR".equals(cargo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas ADM pode ver os logs.");
        }

        List<LogOperacao> logs = logRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}