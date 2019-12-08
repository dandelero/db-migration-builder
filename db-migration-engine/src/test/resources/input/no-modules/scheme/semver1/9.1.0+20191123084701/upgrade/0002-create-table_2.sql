IF NOT EXISTS(SELECT 1 FROM sys.tables where name = 'table_2')
BEGIN
    CREATE TABLE table_2 (
        id bigint PRIMARY KEY IDENTITY (1, 1),
        column_2_1 varchar(32) NOT NULL
    )
END