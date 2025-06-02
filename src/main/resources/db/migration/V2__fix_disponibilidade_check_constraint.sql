    ALTER TABLE disponibilidade
    DROP CONSTRAINT IF EXISTS disponibilidade_dia_da_semana_check;

    ALTER TABLE disponibilidade
    ADD CONSTRAINT chk_disponibilidade_horario CHECK (hora_inicio < hora_fim);