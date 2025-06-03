    # Fluxo da Aplicação - Visão Geral

    Aqui está um diagrama que ilustra o fluxo principal da aplicação, incluindo as camadas de segurança, negócio e persistência.

    ```mermaid
    graph TD
        subgraph "Usuários"
            A[Cliente] -->|Faz requisição| API_G
            B[Profissional] -->|Faz requisição| API_G
            C[Admin] -->|Faz requisição| API_G
        end

        subgraph "Camada de Interface (Controllers)"
            API_G(Requisição REST/HTTP) --> Filter_Chain
            Filter_Chain{Filtros de Segurança}
            Filter_Chain -- Se token não válido/presente --> Auth_C(AuthController - Login)
            Filter_Chain -- Se token válido --> Protected_Endpoints(Outros Controllers Protegidos)
        end

        subgraph "Camada de Segurança (Spring Security + JWT)"
            Auth_C --> S_Auth[1. Login: Email + Senha]
            S_Auth --> Custom_User_S(2. CustomUserDetailsService)
            Custom_User_S --> User_R(3. UserRepository)
            User_R --> DB_Users(4. Consulta Usuário no DB)
            DB_Users --> Custom_User_S
            Custom_User_S --> Encoder(5. Senha validada por PasswordEncoder)
            Encoder --> JWT_S(6. JwtService - Gera JWT)
            JWT_S --> API_Response_JWT(Resposta JWT para o Cliente)

            Protected_Endpoints --> JWT_F(JwtAuthenticationFilter)
            JWT_F --> Validate_JWT(1. Valida JWT via JwtService)
            Validate_JWT -- Token Válido --> Set_Context(2. Define Authentication no SecurityContextHolder)
            Set_Context --> Pre_Authorize(3. @PreAuthorize - Verifica Roles)
            Pre_Authorize -- Autorizado --> Call_Service(Chama Método no Service)
            Pre_Authorize -- Não Autorizado --> Forbidden_Response(403 Forbidden / UnauthorizedException)
        end

        subgraph "Camada de Negócio (Services)"
            Call_Service --> Serv_Impl(Service Impl - Lógica de Negócio)

            subgraph "DisponibilidadeService"
                Serv_Impl -- Criar/Atualizar Disponibilidade --> Disp_Val(1. Validações de Horário e Sobreposição)
                Disp_Val --> Disp_R(2. DisponibilidadeRepository)
                Disp_R --> DB_Disp(3. Persiste/Atualiza Disponibilidade no DB)
                Serv_Impl -- Gerar Slots Disponíveis --> Slot_Logic(1. Lógica de Geração de Slots)
                Slot_Logic --> Disp_R_Read(2. Consulta Disponibilidade no DB)
                Slot_Logic --> Agend_R_Read(3. Consulta Agendamentos no DB)
                Disp_R_Read & Agend_R_Read --> Slot_Logic
                Slot_Logic --> TimeSlot_DTO(4. Retorna Lista de TimeSlotDTO)
            end

            subgraph "AgendamentoService"
                Serv_Impl -- Criar Agendamento --> Agend_Val(1. Validações: Cliente, Profissional, Serviço)
                Agend_Val --> Agend_Disp_Check(2. Valida Disponibilidade Profissional)
                Agend_Val --> Agend_Conflict_Check(3. Valida Conflito de Agendamento)
                Agend_Disp_Check & Agend_Conflict_Check --> Agend_R(4. AgendamentoRepository)
                Agend_R --> DB_Agend(5. Persiste Agendamento no DB)
            end

            subgraph "Outros Services (ServicoService, UserService)"
                Serv_Impl --> Other_Services(Lógica Específica)
                Other_Services --> Other_Repos(Consulta/Persiste em outros Repositories)
            end
        end

        subgraph "Camada de Persistência (Repositories)"
            DB_Users((Banco de Dados - Tabela Users))
            DB_Disp((Banco de Dados - Tabela Disponibilidade))
            DB_Agend((Banco de Dados - Tabela Agendamento))
            Other_Repos((Banco de Dados - Outras Tabelas))
        end

        style API_G fill:#f9f,stroke:#333,stroke-width:2px
        style S_Auth fill:#f9f,stroke:#333,stroke-width:2px
        style JWT_S fill:#f9f,stroke:#333,stroke-width:2px
        style Validate_JWT fill:#f9f,stroke:#333,stroke-width:2px
        style Pre_Authorize fill:#f9f,stroke:#333,stroke-width:2px
        style Call_Service fill:#f9f,stroke:#333,stroke-width:2px
        style Forbidden_Response fill:#fdd,stroke:#333,stroke-width:2px
        style DB_Users fill:#ccf,stroke:#333,stroke-width:2px
        style DB_Disp fill:#ccf,stroke:#333,stroke-width:2px
        style DB_Agend fill:#ccf,stroke:#333,stroke-width:2px
        style Other_Repos fill:#ccf,stroke:#333,stroke-width:2px
    ```

Quando você abrir o arquivo `.md` na sua IDE (ou em uma plataforma que suporte Mermaid), ele deverá renderizar o diagrama automaticamente!

Há mais alguma parte da aplicação que você gostaria de documentar?