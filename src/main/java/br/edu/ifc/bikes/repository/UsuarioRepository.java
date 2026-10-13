package br.edu.ifc.bikes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifc.bikes.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}