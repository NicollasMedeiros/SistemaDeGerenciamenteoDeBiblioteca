package com.biblioteca.model.service;

import com.biblioteca.exception.RegraNegocioException;
import com.biblioteca.model.entity.Autor;
import com.biblioteca.model.entity.Categoria;
import com.biblioteca.model.entity.Livro;
import com.biblioteca.model.repository.AutorRepository;
import com.biblioteca.model.repository.CategoriaRepository;
import com.biblioteca.model.repository.EmprestimoRepository;
import com.biblioteca.model.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LivroService {

    private final LivroRepository livroRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(
            LivroRepository livroRepository,
            CategoriaRepository categoriaRepository,
            AutorRepository autorRepository,
            EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public List<Livro> listar(String busca) {
        if (busca != null && !busca.isBlank()) {
            return livroRepository.buscarComFiltros(busca.trim());
        }
        return livroRepository.findAllComRelacionamentos();
    }

    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Livro não encontrado para o ID: " + id));
    }

    @Transactional
    public Livro salvar(Livro livro) {
        livro.setTitulo(livro.getTitulo().trim());

        if (livro.getIsbn() != null && !livro.getIsbn().isBlank()) {
            String isbnLimpo = livro.getIsbn().trim();
            livroRepository.findByIsbnIgnoreCase(isbnLimpo).ifPresent(existente -> {
                if (!existente.getId().equals(livro.getId())) {
                    throw new RegraNegocioException("Já existe um livro cadastrado com este ISBN.");
                }
            });
            livro.setIsbn(isbnLimpo);
        } else {
            livro.setIsbn(null);
        }

        Categoria categoria = categoriaRepository.findById(livro.getCategoria().getId())
                .orElseThrow(() -> new RegraNegocioException("Categoria selecionada não foi encontrada."));
        livro.setCategoria(categoria);

        if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
            List<Long> idsAutores = livro.getAutores().stream()
                    .map(Autor::getId)
                    .toList();
            List<Autor> autoresGerenciados = autorRepository.findAllById(idsAutores);
            if (autoresGerenciados.size() != idsAutores.size()) {
                throw new RegraNegocioException("Um ou mais autores selecionados não foram encontrados.");
            }
            livro.setAutores(autoresGerenciados);
        }

        if (livro.getId() == null) {
            livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());
        } else {
            Livro existente = buscarPorId(livro.getId());
            int emprestados = existente.getQuantidadeTotal() - existente.getQuantidadeDisponivel();
            if (livro.getQuantidadeTotal() < emprestados) {
                throw new RegraNegocioException(
                        "A quantidade total (" + livro.getQuantidadeTotal()
                                + ") não pode ser menor que a quantidade já emprestada (" + emprestados + ")."
                );
            }
            livro.setQuantidadeDisponivel(livro.getQuantidadeTotal() - emprestados);
        }

        return livroRepository.save(livro);
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = buscarPorId(id);
        if (emprestimoRepository.existsByLivroIdAndDevolvidoFalse(id)) {
            throw new RegraNegocioException("Não é possível excluir um livro que possui empréstimos ativos.");
        }
        livroRepository.delete(livro);
    }
}