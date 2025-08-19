#📝 ForumHub - API REST

##🎯 Sobre o Projeto
O ForumHub é uma API REST que faz uma abstração de fórum de discussão, permitindo operações CRUD completas para gerenciamento de tópicos com autenticação segura via JWT.

##🏗️ Arquitetura Utilizada
Arquitetura em Camadas (Layered Architecture)

Controller Layer: TopicoController, AutenticacaoController

Service Layer: ValidacaoRegra, AutenticacaoService, TokenService

Repository Layer: TopicoRepository, UsuarioRepository

Domain Layer: Topico, Usuario, DTOs

Security Layer: SecurityConfigurations, SecurityFilter

##🔒 Boas Práticas de Segurança Implementadas
###🛡️ Medidas de Proteção:
JWT (JSON Web Tokens) para autenticação stateless

Spring Security com configuração personalizada

BCrypt para hash de senhas

CSRF Protection desabilitado (para APIs REST)

CORS configurado adequadamente

Validação de Dados com Bean Validation

Filtro de Segurança (SecurityFilter) para validação de tokens

Proteção contra tópicos duplicados

Validação de ownership (usuário só mexe nos próprios tópicos)
