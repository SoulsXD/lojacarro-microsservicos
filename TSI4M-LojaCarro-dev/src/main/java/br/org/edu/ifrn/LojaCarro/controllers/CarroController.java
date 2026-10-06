package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // <-- LIBERA O ACESSO PARA O FRONT-END HTML
@RestController
@RequestMapping("/carro")
public class CarroController {

    @Autowired
    private CarroService carroService;

    @PostMapping("salvar")
    public ResponseEntity<Carro> salvarCarro(@RequestBody Carro c, @RequestHeader("idUsuario") Long idUsuario) {
        Carro savedCarro = carroService.save(c, idUsuario);
        return ResponseEntity.ok(savedCarro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizarCarro(@PathVariable Long id, @RequestBody Carro c, @RequestHeader("idUsuario") Long idUsuario) {
        c.setId(id);
        Carro updatedCarro = carroService.update(c, idUsuario);
        return ResponseEntity.ok(updatedCarro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id, @RequestHeader("idUsuario") Long idUsuario) {
        carroService.deleteById(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carro> pesquisarCarroPorId(@PathVariable Long id) {
        Optional<Carro> carro = carroService.findById(id);
        return carro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/listarCarros")
    public ResponseEntity<List<Carro>> pesquisarTodosCarros() {
        List<Carro> carros = carroService.findAll();
        return ResponseEntity.ok(carros);
    }

    @PostMapping(value = "/getCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Carro> pesquisarCarroPorModelo(@RequestBody String modelo) {
        Optional<Carro> carro = carroService.findByModelo(modelo.trim());
        return carro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/deleteCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deletarCarroPorModelo(@RequestBody String modelo, @RequestHeader("idUsuario") Long idUsuario) {
        Carro carro = carroService.deleteByModelo(modelo.trim(), idUsuario);
        return ResponseEntity.ok("Carro deletado: " + carro.getModelo());
    }

    @PostMapping(value = "/updateCarro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Carro> atualizarCarroPorModelo(@RequestBody String payload, @RequestHeader("idUsuario") Long idUsuario) {
        String[] partes = payload.split(",", 2);
        if (partes.length < 2) {
            throw new CarroException("Payload inválido. Use modelo,preco.");
        }
        String modelo = partes[0].trim();
        double preco = parsePreco(partes[1].trim());
        Carro updatedCarro = carroService.updateByModelo(modelo, preco, idUsuario);
        return ResponseEntity.ok(updatedCarro);
    }

    @PostMapping(value = "/teste", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> bomDia(@RequestBody String nome) {
        return ResponseEntity.ok("Bom dia, " + nome.trim());
    }

    private double parsePreco(String preco) {
        try {
            return Double.parseDouble(preco);
        } catch (NumberFormatException ex) {
            throw new CarroException("Preço inválido: " + preco);
        }
    }
}