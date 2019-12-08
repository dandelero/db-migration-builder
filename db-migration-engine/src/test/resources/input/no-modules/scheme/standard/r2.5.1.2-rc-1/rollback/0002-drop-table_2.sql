IF EXISTS(SELECT * FROM sys.tables where name = 'table_2')
BEGIN
    drop table table_2
END