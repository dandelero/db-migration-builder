IF EXISTS(SELECT * FROM sys.tables where name = 'table_1')
BEGIN
    drop table table_1
END