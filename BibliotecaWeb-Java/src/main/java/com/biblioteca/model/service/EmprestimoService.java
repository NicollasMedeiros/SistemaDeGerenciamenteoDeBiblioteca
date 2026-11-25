package com.biblioteca.model.service;

import com.biblioteca.exception.RegraNegocioException;
import com.biblioteca.model.entity.Emprestimo;
import com.biblioteca.model.entity.Livro;
import com.biblioteca.model.entity.Usuario;
import com.biblioteca.model.repository.EmprestimoRepository;
import com.biblioteca.model.repository.LivroRepository;
import com.biblioteca.model.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            LivroRepository livroRepository,
            UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Emprestimo> listar(String status) {
        return emprestimoRepository.buscarPorStatus(status);
    }

    public long contarEmprestimosAtivos() {
        return emprestimoRepository.countByDevolvidoFalse();
    }

    public Emprestimo buscarPorId(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado para o ID: " + id));
    }

    @Transactional
    public Emprestimo registrarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo.getId() != null) {
            return atualizarEmprestimo(emprestimo);
        }

        Livro livro = livroRepository.findById(emprestimo.getLivro().getId())
                .orElseThrow(() -> new RegraNegocioException("Livro selecionado não foi encontrado."));

        Usuario usuario = usuarioRepository.findById(emprestimo.getUsuario().getId())
                .orElseThrow(() -> new RegraNegocioException("Usuário selecionado não foi encontrado."));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException("Não é possível registrar empréstimo para um usuário inativo.");
        }

        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new RegraNegocioException("Não há exemplares disponíveis para o livro selecionado.");
        }

        LocalDate dataEmprestimo = emprestimo.getDataEmprestimo() != null
                ? emprestimo.getDataEmprestimo()
                : LocalDate.now();

        LocalDate dataPrevista = emprestimo.getDataPrevistaDevolucao() != null
                ? emprestimo.getDataPrevistaDevolucao()
                : dataEmprestimo.plusDays(14);

        if (dataPrevista.isBefore(dataEmprestimo)) {
            throw new RegraNegocioException("A data prevista de devolução não pode ser anterior à data do empréstimo.");
        }

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataPrevistaDevolucao(dataPrevista);
        emprestimo.setDevolvido(false);
        emprestimo.setDataDevolucao(null);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo atualizarEmprestimo(Emprestimo dadosAtualizados) {
        Emprestimo existente = buscarPorId(dadosAtualizados.getId());

        LocalDate dataEmprestimo = dadosAtualizados.getDataEmprestimo() != null
                ? dadosAtualizados.getDataEmprestimo()
                : existente.getDataEmprestimo();

        LocalDate dataPrevista = dadosAtualizados.getDataPrevistaDevolucao() != null
                ? dadosAtualizados.getDataPrevistaDevolucao()
                : existente.getDataPrevistaDevolucao();

        if (dataPrevista.isBefore(dataEmprestimo)) {
            throw new RegraNegocioException("A data prevista de devolução não pode ser anterior à data do empréstimo.");
        }

        existente.setDataEmprestimo(dataEmprestimo);
        existente.setDataPrevistaDevolucao(dataPrevista);

        return emprestimoRepository.save(existente);
    }

    @Transactional
    public Emprestimo registrarDevolucao(Long id) {
        Emprestimo emprestimo = buscarPorId(id);

        if (Boolean.TRUE.equals(emprestimo.getDevolvido())) {
            throw new RegraNegocioException("Este empréstimo já foi devolvido.");
        }

        Livro livro = emprestimo.getLivro();
        int novaDisponibilidade = Math.min(livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() + 1);
        livro.setQuantidadeDisponivel(novaDisponibilidade);
        livroRepository.save(livro);

        emprestimo.setDevolvido(true);
        emprestimo.setDataDevolucao(LocalDate.now());

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public void excluir(Long id) {
        Emprestimo emprestimo = buscarPorId(id);

        if (Boolean.FALSE.equals(emprestimo.getDevolvido())) {
            Livro livro = emprestimo.getLivro();
            int novaDisponibilidade = Math.min(livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel() + 1);
            livro.setQuantidadeDisponivel(novaDisponibilidade);
            livroRepository.save(livro);
        }

        emprestimoRepository.delete(emprestimo);
    }
}