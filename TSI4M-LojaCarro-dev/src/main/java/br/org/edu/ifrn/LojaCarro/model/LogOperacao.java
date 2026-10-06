package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class LogOperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private String nome;

    @Column(name = "timestamp_data", nullable = false)
    private LocalDateTime timestampData;

    public LogOperacao() {}

    public LogOperacao(Long idUsuario, String nome, LocalDateTime timestampData) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.timestampData = timestampData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getTimestampData() { return timestampData; }
    public void setTimestampData(LocalDateTime timestampData) { this.timestampData = timestampData; }
}