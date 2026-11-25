package com.biblioteca.model.service;

import com.biblioteca.exception.RegraNegocioException;
import com.biblioteca.model.entity.Autor;
import com.biblioteca.model.repository.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AutorService {

    private final AutorRepository autorRepository;

    public Autor