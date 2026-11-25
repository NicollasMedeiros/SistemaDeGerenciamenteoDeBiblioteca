package com.biblioteca.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public String handleRegraNegocio(RegraNegocioException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:" + referer;
        }

        model.addAttribute("mensagemErro", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleErroGenerico(Exception ex, Model model) {
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado ao processar a solicitação.");
        model.addAttribute("detalheErro", ex.getMessage());
        return "error";
    }
}