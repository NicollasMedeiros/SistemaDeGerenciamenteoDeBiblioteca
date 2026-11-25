package com.biblioteca.model.repository;

import com.biblioteca.model.entity.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    @Query("""
            SELECT e FROM Emprestimo e
            JOIN FETCH e.livro l
            JOIN FETCH e.usuario u
            WHERE (:status IS NULL OR :status = '' OR :status = 'TODOS')
               OR (:status = 'ATIVOS' AND e.devolvido = false)
               OR (:status = 'DEVOLVIDOS' AND e.devolvido = true)
            ORDER BY e.dataEmprestimo DESC, e.id DESC
            """)
    List<Emprestimo> buscarPorStatus(@Param("status") String status);

    long countByDevolvidoFalse();

    boolean existsByLivroIdAndDevolvidoFalse(Long livroId);

    boolean existsByUsuarioIdAndDevolvidoFalse(Long usuarioId);
}