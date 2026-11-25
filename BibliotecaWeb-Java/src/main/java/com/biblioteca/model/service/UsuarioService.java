package com.biblioteca.model.service;

import com.biblioteca.exception.RegraNegocioException;
import com.biblioteca.model.entity.Usuario;
import com.biblioteca.model.repository.EmprestimoRepository;
import com.biblioteca.model.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, EmprestimoRepository emprestimoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public List<Usuario> listar(String busca) {
        if (busca != null && !busca.isBlank()) {
            return usuarioRepository.buscarComFiltros(busca.trim());
        }
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID: " + id));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        usuario.setNome(usuario.getNome().trim());

        String emailLimpo = usuario.getEmail().trim().toLowerCase();
        usuarioRepository.findByEmailIgnoreCase(emailLimpo).ifPresent(existente -> {
            if (!existente.getId().equals(usuario.getId())) {
                throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
            }
        });
        usuario.setEmail(emailLimpo);

        if (usuario.getMatricula() != null && !usuario.getMatricula().isBlank()) {
            String matriculaLimpa = usuario.getMatricula().trim();
            usuarioRepository.findByMatriculaIgnoreCase(matriculaLimpa).ifPresent(existente -> {
                if (!existente.getId().equals(usuario.getId())) {
                    throw new RegraNegocioException("Já existe um usuário cadastrado com esta matrícula.");
                }
            });
            usuario.setMatricula(matriculaLimpa);
        } else {
            usuario.setMatricula(null);
        }

        if (usuario.getTelefone() != null && !usuario.getTelefone().isBlank()) {
            usuario.setTelefone(usuario.getTelefone().trim());
        } else {
            usuario.setTelefone(null);
        }

        if (usuario.getId() == null) {
            if (usuario.getDataCadastro() == null) {
                usuario.setDataCadastro(LocalDate.now());
            }
            if (usuario.getAtivo() == null) {
                usuario.setAtivo(true);
            }
        } else {
            Usuario existente = buscarPorId(usuario.getId());
            usuario.setDataCadastro(existente.getDataCadastro());
            if (usuario.getAtivo() == null) {
                usuario.setAtivo(existente.getAtivo());
            }
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        if (emprestimoRepository.existsByUsuarioIdAndDevolvidoFalse(id)) {
            throw new RegraNegocioException("Não é possível excluir um usuário que possui empréstimos ativos.");
        }
        usuarioRepository.delete(usuario);
    }
}