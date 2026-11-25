package com.biblioteca.controller;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import com.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;
    private final UsuarioService usuarioService;
    private final LivroService livroService;

    public EmprestimoController(EmprestimoService emprestimoService,
                                UsuarioService usuarioService,
                                LivroService livroService) {
        this.emprestimoService = emprestimoService;
        this.usuarioService = usuarioService;
        this.livroService = livroService;
    }

    private void carregarOpcoesFormulario(Model model) {
        model.addAttribute("usuarios", usuarioService.listar(null));
        model.addAttribute("livros", livroService.listar(null));
    }

    private void prepararEntidadeParaFormulario(Emprestimo emprestimo) {
        if (emprestimo.getUsuario() == null) {
            emprestimo.setUsuario(new Usuario());
        }
        if (emprestimo.getLivro() == null) {
            emprestimo.setLivro(new Livro());
        }
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("emprestimos", emprestimoService.listar(busca));
        model.addAttribute("busca", busca);
        return "emprestimo/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Emprestimo emprestimo = new Emprestimo();
        prepararEntidadeParaFormulario(emprestimo);
        model.addAttribute("emprestimo", emprestimo);
        carregarOpcoesFormulario(model);
        return "emprestimo/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("emprestimo") Emprestimo emprestimo,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepararEntidadeParaFormulario(emprestimo);
            carregarOpcoesFormulario(model);
            return "emprestimo/formulario";
        }

        try {
            emprestimoService.registrarEmprestimo(emprestimo);
            redirectAttributes.addFlashAttribute("sucesso", "Empréstimo registrado com sucesso!");
            return "redirect:/emprestimos";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/emprestimos";
        }
    }

    @PostMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            emprestimoService.devolver(id);
            redirectAttributes.addFlashAttribute("sucesso", "Devolução registrada com sucesso!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/emprestimos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            emprestimoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Empréstimo excluído com sucesso!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/emprestimos";
    }
}