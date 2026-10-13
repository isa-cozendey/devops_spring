package br.edu.ifc.bikes.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.edu.ifc.bikes.dto.AtualizarSenhaRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioResponseDTO;
import br.edu.ifc.bikes.dto.mapper.UsuarioMapper;
import br.edu.ifc.bikes.entity.Usuario;
import br.edu.ifc.bikes.exception.EntityNotFoundException;
import br.edu.ifc.bikes.exception.InvalidPasswordException;
import br.edu.ifc.bikes.exception.UsernameUniqueVioletionException;
import br.edu.ifc.bikes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDTO create(UsuarioRequestDTO usuarioRequestDTO) {
        try{
            Usuario usuario = usuarioMapper.toUsuario(usuarioRequestDTO);
            return usuarioMapper.toResponse(usuarioRepository.save(usuario));
        } catch (DataIntegrityViolationException e) {
            throw new UsernameUniqueVioletionException(String.format("O nome do usuário %s já está em uso.", usuarioRequestDTO.username()));
        }
    }

    @Transactional()
    public UsuarioResponseDTO getById(Long id) {
        return usuarioMapper.toResponse(usuarioRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Usuário com ID %d não encontrado.", id))));
    }

    @Transactional
    public void updatePassword(Long id, AtualizarSenhaRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Usuário com ID %d não encontrado.", id)));

        if (!usuario.getPassword().equals(request.senhaAtual())) {
            throw new InvalidPasswordException("A senha atual está incorreta.");
        }

        if (!request.novaSenha().equals(request.confirmarSenha())) {
            throw new InvalidPasswordException("A nova senha e a confirmação não correspondem.");
        }

        usuario.setPassword(request.novaSenha());
        usuarioRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> getAll(){
        return usuarioMapper.toResponse(usuarioRepository.findAll());
    }
}