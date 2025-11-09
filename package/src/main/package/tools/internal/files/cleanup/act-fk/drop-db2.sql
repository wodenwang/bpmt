alter table ACT_GE_BYTEARRAY drop foreign key ACT_FK_BYTEARR_DEPL;
alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_PROCDEF;
alter table ACT_RU_IDENTITYLINK  drop FOREIGN KEY ACT_FK_ATHRZ_PROCEDEF;
alter table ACT_RU_TASK drop foreign key ACT_FK_TASK_PROCDEF;
alter table ACT_RE_MODEL drop foreign key ACT_FK_MODEL_SOURCE;
alter table ACT_RE_MODEL drop foreign key ACT_FK_MODEL_SOURCE_EXTRA;
alter table ACT_RE_MODEL drop foreign key ACT_FK_MODEL_DEPLOYMENT;