alter table ACT_GE_BYTEARRAY add constraint ACT_FK_BYTEARR_DEPL foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT (ID_);
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_PROCDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_);
alter table ACT_RU_IDENTITYLINK add constraint ACT_FK_ATHRZ_PROCEDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF(ID_);
alter table ACT_RU_TASK add constraint ACT_FK_TASK_PROCDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_);
alter table ACT_RE_MODEL add constraint ACT_FK_MODEL_SOURCE foreign key (EDITOR_SOURCE_VALUE_ID_) references ACT_GE_BYTEARRAY (ID_);
alter table ACT_RE_MODEL add constraint ACT_FK_MODEL_SOURCE_EXTRA foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) references ACT_GE_BYTEARRAY (ID_);
alter table ACT_RE_MODEL add constraint ACT_FK_MODEL_DEPLOYMENT foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT (ID_);