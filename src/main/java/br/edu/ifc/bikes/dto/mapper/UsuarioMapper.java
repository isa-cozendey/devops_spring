package br.edu.ifc.bikes.dto.mapper;

import br.edu.ifc.bikes.dto.UsuarioRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioResponseDTO;
import br.edu.ifc.bikes.entity.Usuario;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toUsuario(UsuarioRequestDTO usuarioRequestDTO);
    UsuarioResponseDTO toResponse(Usuario usuario);
    List<UsuarioResponseDTO> toResponse(List<Usuario> usuarios);
}