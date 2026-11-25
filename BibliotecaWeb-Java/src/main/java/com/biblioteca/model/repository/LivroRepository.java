package com.biblioteca.model.repository;

import com.biblioteca.model.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query("""
            SELECT DISTINCT l FROM Livro l
            LEFT JOIN FETCH l.categoria
            LEFT JOIN FETCH l.autores
            WHERE :busca IS NULL
               OR :busca = ''
               OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(l.isbn, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            ORDER BY l.titulo ASC
            """)
    List<Livro> buscarComFiltros(@Param("busca") String busca);

    @Query("""
            SELECT DISTINCT l FROM Livro l
            LEFT JOIN FETCH l.categoria
            LEFT JOIN FETCH l.autores
            ORDER BY l.titulo ASC
            """)
    List<Livro> findAllComRelacionamentos();

    Optional<Livro> findByIsbnIgnoreCase(String isbn);
}