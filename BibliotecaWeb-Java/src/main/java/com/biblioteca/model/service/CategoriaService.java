package com.biblioteca.model.service;

import com.biblioteca.exception.RegraNegocioException;
import com.biblioteca.model.entity.Categoria;
import com.biblioteca.model.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listar(String busca) {
        if (busca != null && !busca.isBlank()) {
            return categoriaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim());
        }
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada para o ID: " + id));
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        String nomeLimpo = categoria.getNome().trim();
        categoriaRepository.findByNomeIgnoreCase(nomeLimpo).ifPresent(existente -> {
            if (!existente.getId().equals(categoria.getId())) {
                throw new RegraNegocioException("Já existe uma categoria cadastrada com este nome.");
            }
        });

        categoria.setNome(nomeLimpo);
        if (categoria.getDescricao() != null) {
            categoria.setDescricao(categoria.getDescricao().trim());
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        if (!categoria.getLivros().isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir uma categoria que possui livros vinculados.");
        }
        categoriaRepository.delete(categoria);
    }
}