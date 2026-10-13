package br.edu.ifc.bikes.dto;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String role
) {}