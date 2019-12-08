IF NOT EXISTS(SELECT 1 FROM sys.tables where name = 'table_1')
BEGIN
    CREATE TABLE table_1 (
        id bigint PRIMARY KEY IDENTITY (1, 1),
        column_1_1 varchar(32) NOT NULL
    )
END


