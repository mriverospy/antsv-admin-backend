ALTER TABLE tipo_organizacion ADD COLUMN codigo VARCHAR(6);
UPDATE tipo_organizacion set codigo = 'AC' WHERE nombre = 'Academia';
UPDATE tipo_organizacion set codigo = 'EMP' WHERE nombre = 'Empresas';
UPDATE tipo_organizacion set codigo = 'EG' WHERE nombre = 'Entidades de Gobierno';
UPDATE tipo_organizacion set codigo = 'SCAG' WHERE nombre = 'Sociedad Civil, Asociaciones o Gremios';
UPDATE tipo_organizacion set codigo = 'ONG' WHERE nombre = 'Organizaciones sin files de lucro';
UPDATE tipo_organizacion set codigo = 'SU' WHERE nombre = 'Startups';
UPDATE tipo_organizacion set codigo = 'SO' WHERE nombre = 'Sin organización formal';