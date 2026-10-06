package br.org.edu.ifrn.apiusuarios;

import br.org.edu.ifrn.apiusuarios.model.Cargo;
import br.org.edu.ifrn.apiusuarios.model.Usuario;
import br.org.edu.ifrn.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiUsuariosApplication implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public static void main(String[] args) {
        SpringApplication.run(ApiUsuariosApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail("admin@lojacarro.com").isEmpty()) {
            Usuario adminGeral = new Usuario();
            adminGeral.setNome("Administrador Geral");
            adminGeral.setEmail("admin@lojacarro.com");
            adminGeral.setSenha("admin123");
            adminGeral.setCargo(Cargo.ADMINISTRADOR);

            usuarioRepository.save(adminGeral);

            System.out.println("=========================================================");
            System.out.println("Administrador Geral criado");
            System.out.println("E-mail: admin@lojacarro.com");
            System.out.println("Senha: admin123");
            System.out.println("=========================================================");
        }
    }
}