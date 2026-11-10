package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarroService {

    @Autowired
    public CarroRepository carroRepository;

    @Autowired
    private LogProducerService logService;

    private void registrarLog(String operacao, Carro carro, Long idUsuario) {
        Long idEntidade = (carro != null) ? carro.getId() : null;
        logService.enviarLog("CARRO", operacao, idEntidade, idUsuario);
    }

    public Carro save(Carro c, Long idUsuario) {
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());
        Carro salvo = carroRepository.save(c);

        registrarLog("CRIAR", salvo, idUsuario);
        return salvo;
    }

    public void deleteById(Long id, Long idUsuario) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        Optional<Carro> carro = carroRepository.findById(id);
        carroRepository.deleteById(id);

        carro.ifPresent(c -> registrarLog("DELETAR", c, idUsuario));
    }

    public Optional<Carro> findById(Long id) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        return carroRepository.findById(id);
    }

    public List<Carro> findAll() {
        return carroRepository.findAll();
    }

    public Optional<Carro> findByModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo);
    }

    public Carro saveFromLegacy(String modelo, double preco, Long idUsuario) {
        Carro carro = new Carro(modelo, LocalDate.now().getYear(), preco);
        return save(carro, idUsuario);
    }

    public Carro updateByModelo(String modelo, double preco, Long idUsuario) {
        Carro carro = localizarCarroPorModelo(modelo);
        validarPreco(preco);
        carro.setPreco(preco);
        Carro atualizado = carroRepository.save(carro);

        registrarLog("ATUALIZAR_PRECO", atualizado, idUsuario);
        return atualizado;
    }

    public Carro deleteByModelo(String modelo, Long idUsuario) {
        Carro carro = localizarCarroPorModelo(modelo);
        carroRepository.delete(carro);

        registrarLog("DELETAR", carro, idUsuario);
        return carro;
    }

    public Carro update(Carro c, Long idUsuario) {
        if (c.getId() == null) {
            throw new CarroException("O ID do carro para atualização não pode ser nulo.");
        }
        if (!carroRepository.existsById(c.getId())) {
            throw new CarroException("Carro com ID " + c.getId() + " não encontrado para atualização.");
        }
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());
        Carro atualizado = carroRepository.save(c);

        registrarLog("ATUALIZAR", atualizado, idUsuario);
        return atualizado;
    }

    private void validarModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new CarroException("O modelo do carro não pode estar vazio.");
        }
        if (modelo.length() >= 10) {
            throw new CarroException("O modelo do carro deve ter menos de 10 caracteres. Tamanho atual: " + modelo.length());
        }
    }

    private void validarPreco(double preco) {
        if (preco < 0) {
            throw new CarroException("O preço do carro não pode ser negativo. Valor fornecido: " + preco);
        }
    }

    private Carro localizarCarroPorModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo)
                .orElseThrow(() -> new CarroException("Carro com modelo " + modelo + " não encontrado."));
    }
}