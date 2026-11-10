package br.org.edu.ifrn.apicontabil.services;

import br.org.edu.ifrn.apicontabil.mensageria.Enviar;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogProducerService {

    @Autowired
    private Enviar enviarRabbit;

    public void enviarLog(String modulo, String operacao, Long idEntidade, Long idUsuario) {
        try {
            Map<String, Object> logDados = new HashMap<>();
            logDados.put("modulo", modulo);
            logDados.put("operacao", operacao);
            logDados.put("idEntidade", idEntidade);
            logDados.put("idUsuario", idUsuario);

            String json = new ObjectMapper().writeValueAsString(logDados);
            enviarRabbit.enviarMensagem(json, "fila_log_marcelo");
            System.out.println("LOG ENVIADO (" + modulo + "): " + json);
        } catch (Exception e) {
            System.err.println("Erro ao gerar " + e.getMessage());
        }
    }
}