package br.org.edu.ifrn.apicontabil.mensageria;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class Enviar {

    public void enviarMensagem(String mensagem, String nomeFila) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("15.228.215.36");
        factory.setPort(5672);
        factory.setUsername("aluno");
        factory.setPassword("12345");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(nomeFila, true, false, false, null);
            channel.basicPublish("", nomeFila, null, mensagem.getBytes(StandardCharsets.UTF_8));
            System.out.println("APIVENDAS - RABBITMQ Enviado para '" + nomeFila + "': " + mensagem);

        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem da Loja: " + e.getMessage());
        }
    }
}