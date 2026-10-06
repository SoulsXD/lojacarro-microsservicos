package br.org.edu.ifrn.apiusuarios.controllers;

import br.org.edu.ifrn.apiusuarios.UsuarioException;
import br.org.edu.ifrn.apiusuarios.model.Cargo;
import br.org.edu.ifrn.apiusuarios.model.Usuario;
import br.org.edu.ifrn.apiusuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody Usuario usuario,
            @RequestHeader("Cargo-Usuario-Logado") Cargo cargoLogado,
            @RequestHeader("Id-Usuario-Logado") Long idLogado) { // Novo Cabeçalho
        try {
            Usuario salvo = service.salvar(usuario, cargoLogado, idLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @RequestHeader("Cargo-Usuario-Logado") Cargo cargoLogado,
            @RequestHeader("Id-Usuario-Logado") Long idLogado) { // Novo Cabeçalho
        try {
            service.deletar(id, cargoLogado, idLogado);
            return ResponseEntity.noContent().build();
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Usuario credenciais) {
        try {
            Usuario usuarioLogado = service.autenticar(credenciais.getEmail(), credenciais.getSenha());
            return ResponseEntity.ok(usuarioLogado);
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}