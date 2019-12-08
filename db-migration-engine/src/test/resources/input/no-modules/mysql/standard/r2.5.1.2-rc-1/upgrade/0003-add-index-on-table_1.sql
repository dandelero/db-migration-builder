IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID('dbo.table_1') AND name='table_1_ix_col_1')
BEGIN
    CREATE UNIQUE INDEX table_1_ix_col_1
       ON table_1(column_1_1);
END