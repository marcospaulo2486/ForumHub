# 📝 ForumHub - API REST

## 🎯 Sobre o Projeto
API REST para fórum de discussão com CRUD completo de tópicos e autenticação JWT.

## 🏗️ Arquitetura
Arquitetura em Camadas (Layered Architecture)
- Controller Layer
- Service Layer  
- Repository Layer
- Domain Layer
- Security Layer

## 🔒 Segurança Implementada

- Autenticação JWT
- Spring Security Configurado
- BCrypt para senhas
- CSRF Protection (APIs REST)
- Validação de Dados Bean Validation
- Security Filter para tokens
- Proteção contra duplicados
- Validação de ownership
## 📦 Dependências Principais
- Spring Boot Starter Web
Spring Boot Starter Data JPA
Spring Boot Starter Validation
Spring Boot Starter Security
Java JWT (auth0)
MySQL Connector
Flyway Core
Lombok
