package br.edu.ifc.bikes.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

//===lombok
@Getter
@Setter
@ToString
//===lombok

@Entity
@Table(name = "usuarios")
public class Usuario implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    //a constante definida no enum passa a ser uma string no bd
    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false, length = 50)
    private Role role = Role.CLIENTE;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataModificacao;
    private String criadoPor;
    private String modificadoPor;

    public enum Role {
        ADMIN, CLIENTE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(id, usuario.id);
    }
}