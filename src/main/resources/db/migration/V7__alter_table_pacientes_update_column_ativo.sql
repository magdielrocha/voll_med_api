update pacientes
set ativo = 1
where ativo is null;

alter table pacientes
    modify ativo tinyint not null default 1;