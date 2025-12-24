# 📚 exemplo_Jwt — CRUD de Usuário com Spring Boot, Spring Security e JWT

Este projeto tem **fins exclusivamente didáticos** e foi criado para demonstrar,
na prática, os conceitos fundamentais do **Spring Boot**, **Spring Security**,
**JWT**, **JPA/Hibernate** e boas práticas de organização de código em um
**CRUD de Usuário**.

Ele serve como base de estudo para quem está aprendendo backend com Java e Spring.

---

## 🛠 Tecnologias e Versões Utilizadas

As tecnologias abaixo são exatamente as utilizadas no projeto:

| Tecnologia | Versão |
|----------|--------|
| Java | 17 |
| Spring Boot | 3.5.8 |
| Spring Security | 6.x (via Spring Boot 3) |
| Spring Data JPA | 3.x |
| Hibernate | 6.x |
| JWT (JJWT) | 0.11.5 |
| Banco de Dados | H2 (em memória) |
| Maven | Gerenciador de dependências |
| Lombok | Para reduzir código padrão |

---

## 🧱 Estrutura do Projeto

A estrutura segue uma separação clara de responsabilidades:

```bash
src/main/java/com/example/exemplo_Jwt
│
├── controller
│   └── UsuarioController            # Recebe requisições HTTP e expõe endpoints REST
│
├── dto
│   ├── LoginRequestDTO              # Dados de entrada para autenticação (login)
│   ├── LoginResponseDTO             # Dados de saída com o token JWT
│   ├── UsuarioRequestDTO            # Dados de entrada para cadastro/atualização
│   ├── UsuarioResponseDTO           # Dados de saída do usuário
│   └── mapper
│       └── UsuarioMapper            # Conversão entre DTOs e Entity
│
├── entity
│   └── UsuarioEntity                # Entidade JPA (tabela de usuários)
│
├── repository
│   └── UsuarioRepository            # Acesso ao banco de dados (Spring Data JPA)
│
├── security
│   ├── CustomUserDetailsService     # Integra usuários do banco ao Spring Security
│   ├── JwtAuthenticationFilter      # Filtro que valida o JWT em cada requisição
│   └── SecurityConfig               # Configuração de segurança da aplicação
│
├── service
│   ├── JwtService                   # Geração e validação de tokens JWT
│   └── UsuarioService               # Regras de negócio do usuário
│
└── ExemploJwtApplication             # Classe principal do Spring Boot
                                     # Responsável por iniciar a aplicação
```


---

## 📌 Descrição das Camadas

### 🔹 Controller

- Responsável por receber requisições HTTP
- Expõe endpoints REST
- Recebe e retorna DTOs
- Não contém regra de negócio

Anotações importantes usadas:
- `@RestController`
- `@RequestMapping`
- `@PostMapping`, `@GetMapping`, etc
- `@RequestBody`
- `@PathVariable`

---

### 🔹 DTO (Data Transfer Object)

O projeto utiliza DTOs para:
- Evitar expor diretamente a entidade
- Controlar entrada e saída de dados
- Melhorar segurança e organização

DTOs existentes:
- `LoginRequestDTO`
- `LoginResponseDTO`
- `UsuarioRequestDTO`
- `UsuarioResponseDTO`

---

### 🔹 Mapper

- Classe responsável por converter:
  - `UsuarioEntity → UsuarioResponseDTO`
  - `UsuarioRequestDTO → UsuarioEntity`
- Centraliza conversões
- Evita duplicação de código

---

### 🔹 Entity (JPA)

- Representa a tabela de usuário no banco
- Usa JPA/Hibernate
- Contém anotações como:
  - `@Entity`
  - `@Id`
  - `@GeneratedValue`
  - `@Column`

---

### 🔹 Repository

- Interface que estende `JpaRepository`
- Responsável por acessar o banco de dados
- O Spring Data JPA gera as implementações automaticamente

---

### 🔹 Service

- Contém a lógica da aplicação
- Coordena regras de negócio
- Não depende de HTTP nem de persistência direta

Services existentes:
- `UsuarioService`
- `JwtService`

---

## 🔐 Spring Security e JWT (Visão Didática)

### 🔹 SecurityConfig

- Classe de configuração do Spring Security
- Define:
  - Quais rotas são públicas
  - Quais exigem autenticação
- Configura a aplicação como **stateless**
- Registra o filtro JWT

---

### 🔹 JwtAuthenticationFilter

- Executado a cada requisição
- Responsável por:
  - Ler o token JWT do header `Authorization`
  - Validar o token
  - Autenticar o usuário no contexto de segurança

---

### 🔹 CustomUserDetailsService

- Implementa `UserDetailsService`
- Carrega o usuário do banco
- Informa ao Spring Security:
  - Username
  - Password
  - Permissões (quando existirem)

---

### 🔹 JwtService

- Responsável por:
  - Gerar tokens JWT
  - Validar tokens
  - Extrair informações do token (ex: username)

---

## 🗄 Banco de Dados

- Utiliza **H2 em memória**
- Ideal para estudos e testes
- Nenhuma configuração externa é necessária

---

## 🎯 Objetivo do Projeto

Este projeto tem como objetivo ensinar:

- Organização de um projeto Spring Boot
- Funcionamento básico do Spring Security
- Autenticação com JWT
- Uso de JPA com DTOs e Mapper
- Conceitos modernos do Java (Streams, Lambda)

---

## ⚠️ Observações Importantes

- Projeto **não é voltado para produção**
- Focado em aprendizado
- Segurança simplificada para fins didáticos

---

📘 **Projeto criado para estudo, leitura de código e experimentação.**
