-- Create the change_log table
create table change_log (
    change_log_id bigint PRIMARY KEY IDENTITY (1, 1),
    module_name varchar(32) NOT NULL,
    release_label varchar(32) NOT NULL,
    sequence_number bigint NOT NULL,
    script_author varchar(64) NOT NULL,
    file_name varchar(64) NOT NULL,
    applied_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    applied_by varchar(32) default CURRENT_USER
)
GO

-- Ensure there can only be 1 record for every delta script in a release.
ALTER TABLE change_log
    ADD CONSTRAINT
        change_log_uk
    UNIQUE
        (module_name, release_label, sequence_number);
GO
