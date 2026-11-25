package com.biblioteca.controller;

import com.biblioteca.model.service.AutorService;
import com.biblioteca.model.service.CategoriaService;
import com.biblioteca.model.service.EmprestimoService;
import com.biblioteca.model.service.LivroService;
import com.biblioteca.model.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final LivroService livroService;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final EmprestimoService emprestimoService;

    public HomeController(
            LivroService livroService,
            AutorService autorService,
            CategoriaService categoriaService,
            UsuarioService usuarioService,
            EmprestimoService emprestimoService) {
        this.livroService = livroService;
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalLivros", livroService.contarTodos());
        model.addAttribute("totalAutores", autorService.contarTodos());
        model.addAttribute("totalCategorias", categoriaService.contarTodos());
        model.addAttribute("totalUsuarios", usuarioService.contarTodos());
        model.addAttribute("totalEmprestimosAtivos", emprestimoService.contarAtivos());
        return "index";
    }
}
