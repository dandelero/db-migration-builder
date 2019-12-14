-- Create the change_log table
create table change_log (
    change_log_id INT(11) NOT NULL AUTO_INCREMENT,
    module_name varchar(32) NOT NULL,
    release_label varchar(32) NOT NULL,
    sequence_number bigint NOT NULL,
    script_author varchar(64) NOT NULL,
    file_name varchar(64) NOT NULL,
    applied_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    applied_by varchar(32),
    CONSTRAINT change_log_pk PRIMARY KEY (change_log_id)
)

-- Because mysql cannot call user/current_user to set a default value for a column we have to use a trigger:
create or replace trigger trigger_change_log_insert
    after insert on change_log
    for each row
        update change_log
        set applied_by = current_user
        where change_log_id = new.change_log_id

DELIMITER $$
CREATE TRIGGER `trigger_change_log_insert` BEFORE INSERT ON `change_log`
FOR EACH ROW BEGIN
  SET NEW.applied_by = current_user;
END;
$$
DELIMITER ;


CREATE UNIQUE INDEX change_log_uk ON change_log(release_label, sequence_number);
