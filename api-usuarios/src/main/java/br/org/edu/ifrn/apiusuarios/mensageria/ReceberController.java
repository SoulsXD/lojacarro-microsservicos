package br.org.edu.ifrn.apiusuarios.mensageria;

import br.org.edu.ifrn.apiusuarios.model.LogOperacao;
import br.org.edu.ifrn.apiusuarios.repository.LogOperacaoRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
public class ReceberController {

    @Autowired
    private LogOperacaoRepository logOperacaoRepository;

    @GetMapping("/ler-log")
    public String lerMensagemManualmente() {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setHost("15.228.215.36");
            factory.setPort(5672);
            factory.setUsername("aluno");
            factory.setPassword("12345");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                String QUEUE_NAME = "fila_log_marcelo";
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                GetResponse response = channel.basicGet(QUEUE_NAME, true);

                if (response != null) {
                    String mensagemJson = new String(response.getBody(), StandardCharsets.UTF_8);

                    System.out.println("JSON LIDO" + mensagemJson);

                    LogOperacao log = new LogOperacao(1L, mensagemJson, LocalDateTime.now());
                    logOperacaoRepository.save(log);

                    return "Mensagem salva no banco: " + mensagemJson;
                } else {
                    return "fila vazia.";
                }
            }
        } catch (Exception e) {
            return "Erro ao ligar ao RabbitMQ: " + e.getMessage();
        }
    }
}