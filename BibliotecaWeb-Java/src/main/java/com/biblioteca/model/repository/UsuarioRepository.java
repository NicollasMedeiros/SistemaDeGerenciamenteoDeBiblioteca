package com.biblioteca.model.repository;

import com.biblioteca.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
            SELECT u FROM Usuario u
            WHERE :busca IS NULL
               OR :busca = ''
               OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(u.matricula, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            ORDER BY u.nome ASC
            """)
    List<Usuario> buscarComFiltros(@Param("busca") String busca);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByMatriculaIgnoreCase(String matricula);
}