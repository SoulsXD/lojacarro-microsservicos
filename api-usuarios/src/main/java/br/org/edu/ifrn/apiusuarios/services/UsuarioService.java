package br.org.edu.ifrn.apiusuarios.services;

import br.org.edu.ifrn.apiusuarios.UsuarioException;
import br.org.edu.ifrn.apiusuarios.model.Cargo;
import br.org.edu.ifrn.apiusuarios.model.Usuario;
import br.org.edu.ifrn.apiusuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;

    private String getDataHoraAtual() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Usuario salvar(Usuario novoUsuario, Cargo cargoUsuarioLogado, Long idUsuarioLogado) {
        String data = getDataHoraAtual();

        if (cargoUsuarioLogado == Cargo.CLIENTE) {
            log.warn("Data: {} | Ação: Tentativa de cadastro negada (Sem permissão) | Autor ID: {}", data, idUsuarioLogado);
            throw new UsuarioException("Erro: Clientes não têm permissão para cadastrar outros usuários no sistema.");
        }

        if (cargoUsuarioLogado == Cargo.VENDEDOR && novoUsuario.getCargo() != Cargo.CLIENTE) {
            log.warn("Data: {} | Ação: Tentativa de cadastro negada (Vendedor tentou cadastrar {}) | Autor ID: {}", data, novoUsuario.getCargo(), idUsuarioLogado);
            throw new UsuarioException("Erro: Vendedores só podem cadastrar clientes.");
        }

        if (repository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            log.warn("Data: {} | Ação: Tentativa de cadastro falhou (E-mail duplicado) | Autor ID: {}", data, idUsuarioLogado);
            throw new UsuarioException("Erro: Este e-mail já está em uso.");
        }

        Usuario salvo = repository.save(novoUsuario);
        log.info("Data: {} | Ação: Novo usuário criado (ID: {}) | Autor ID: {}", data, salvo.getId(), idUsuarioLogado);
        return salvo;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Ação: Tentativa de buscar um usuário inexistente (ID: {})", id);
            return new UsuarioException("Usuário não encontrado com o ID: " + id);
        });
    }

    public void deletar(Long id, Cargo cargoUsuarioLogado, Long idUsuarioLogado) {
        String data = getDataHoraAtual();

        if (cargoUsuarioLogado != Cargo.ADMINISTRADOR) {
            log.warn("Data: {} | Ação: Tentativa de exclusão negada (Sem permissão) | Autor ID: {}", data, idUsuarioLogado);
            throw new UsuarioException("Erro: Apenas administradores podem excluir usuários.");
        }

        if (id.equals(idUsuarioLogado)) {
            log.warn("Data: {} | Ação: Tentativa de autoexclusão bloqueada | Autor ID: {}", data, idUsuarioLogado);
            throw new UsuarioException("Erro: Você não pode excluir a sua própria conta.");
        }

        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);

        log.info("Data: {} | Ação: Usuário (ID: {}) excluído com sucesso | Autor ID: {}", data, id, idUsuarioLogado);
    }

    public Usuario autenticar(String email, String senha) {
        log.info("Tentativa de login para o e-mail: {}", email);

        Usuario usuario = repository.findByEmail(email).orElseThrow(() -> {
            log.warn("Falha no login: E-mail {} não encontrado.", email);
            return new UsuarioException("E-mail não encontrado.");
        });

        if (!usuario.getSenha().equals(senha)) {
            log.warn("Falha no login: Senha incorreta para o e-mail {}.", email);
            throw new UsuarioException("Senha incorreta.");
        }

        log.info("Sucesso: Usuário {} (ID: {}) logado com sucesso.", usuario.getNome(), usuario.getId());
        return usuario;
    }
}