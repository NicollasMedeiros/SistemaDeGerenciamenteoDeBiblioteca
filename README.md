# Sistema de Gestão de Biblioteca (`biblioteca-web`)

Aplicação web desenvolvida em Java com Spring Boot para o gerenciamento completo de acervo, autores, categorias, usuários e empréstimos de uma biblioteca.

## Funcionalidades

- **Dashboard Inicial (`/`)**: Visão geral e navegação rápida entre os módulos do sistema.
- **Gestão de Livros (`/livros`)**: Cadastro, consulta, edição e controle de disponibilidade de exemplares, incluindo validação de campos obrigatórios (como título, categoria, ano de publicação, quantidade total e autores).
- **Gestão de Autores (`/autores`)**: Cadastro, listagem e vínculo de autores às obras do acervo.
- **Gestão de Categorias (`/categorias`)**: Organização e classificação dos livros por gênero ou área de conhecimento.
- **Gestão de Usuários (`/usuarios`)**: Cadastro de leitores com validação de e-mail e matrícula única, além de controle de status ativo/inativo.
- **Controle de Empréstimos (`/emprestimos`)**: Registro de retirada e devolução de livros, impedindo a exclusão de usuários com empréstimos ativos.

## Stack Utilizada

- **Java 21**
- **Spring Boot 3.4.3**
  - Spring Web (Spring MVC)
  - Spring Data JPA / Hibernate
  - Thymeleaf (Motor de templates HTML)
  - Validação de Bean (`spring-boot-starter-validation`)
  - Ferramentas de desenvolvimento do Spring Boot
- **Banco de Dados**: Banco de Dados H2 (em memória para desenvolvimento e testes locais)
- **Gerenciamento de Dependências**: Apache Maven
- **Variáveis de Ambiente**: `spring-dotenv`

## Arquitetura do Projeto

O projeto segue o padrão arquitetural MVC (Model-View-Controller) com camada de serviço isolada:

```text
src/main/
├── java/com/biblioteca/
│   ├── controller/        # Controladores Spring MVC (rotas e renderização de views)
│   ├── exception/         # Exceções customizadas de regra de negócio
│   ├── model/
│   │   ├── entity/        # Entidades JPA mapeadas para o banco de dados
│   │   ├── repository/    # Interfaces Spring Data JPA
│   │   └── service/       # Regras de negócio e controle transacional
│   └── BibliotecaApplication.java
└── resources/
    ├── static/            # Arquivos estáticos (CSS, JS, imagens)
    ├── templates/         # Páginas HTML com Thymeleaf
    └── application.properties
