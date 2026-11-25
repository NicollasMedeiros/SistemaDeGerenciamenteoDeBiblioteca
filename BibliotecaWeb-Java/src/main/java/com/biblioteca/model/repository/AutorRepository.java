package com.biblioteca.model.repository;

import com.biblioteca.model.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    List<Autor> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Autor> findAllByOrderByNomeAsc();
}