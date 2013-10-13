SET FOREIGN_KEY_CHECKS=0;

insert into muser(ID, ACCESSDENIEDTIMES, EMAIL, FREEZENFLAG, FULLNAME, LOCKFLAG, LOGINFAILEDTIMES, LOGINTIMES, NEEDRESETPWD, PASSWORD, USERNAME, CREATED_BY, TENANT_ID, UPDATED_BY) values(1, 0, 'hailing@126.com', 0, 'tracylu', 0, 0, 1, 0, '123456', 'ANONYMOUS', 1, 1, 1);

insert into tenant(ID, ADDRESS, ARREARAGE, ARTIFICIAL_PERSON_NAME, DELETED, EVALUATED, FREEZEN, HISTORY_SERVICE_DAYS, LOCKED, MATURITY_DATE, NAME, PAYMENT, SERVICE_DAYS_LEFT, SERVICE_DAYS_PAID, CREATED_BY, UPDATED_BY, ADMIN_USER_ID) values(1, 'Beijing Beichen East Road',0, 'Barryzhong',0,0,0,100,0, '2015-10-4','北京风云科技有限公司', 1000, 730, 100, 1,1,1);

insert into service_summary_plan(TENANT_ID, ID, CREATED_BY, PRODUCE_SPEC_ID, UPDATED_BY ) values(1,1, 1, 1, 1);

insert into mixing_plant_resource(TENANT_ID, ID, FINISHED_VOLUME,PLANNED_VOLUME ,CREATED_BY, UPDATED_BY, MIXING_PLANT_ID) values(1, 1, 300, 3000,1,1, 1);

insert into mixing_plant(TENANT_ID, ID, NAME, PINYIN_ABBR_NAME, CREATED_BY, UPDATED_BY, OPERATOR_ID) values(1, 1, '第三号搅拌站', 'dshjbz', 1, 1, 2);

insert into muser(ID, ACCESSDENIEDTIMES, EMAIL, FREEZENFLAG, FULLNAME, LOCKFLAG, LOGINFAILEDTIMES, LOGINTIMES, NEEDRESETPWD, PASSWORD, USERNAME, CREATED_BY, TENANT_ID, UPDATED_BY) values(2, 0, 'superdingdang@126.com', 0, 'superdingdang', 0, 0, 1, 0, '1234567', 'superdingdang', 1, 1, 1);

insert into concrete_truck_resource(TENANT_ID, ID, CREATED_BY, UPDATED_BY, CONCRETE_TRUCK_ID) values(1, 1, 1, 1, 1);

insert into concrete_truck(TENANT_ID, ID, LICENCE_PLATE_NUMBER, RATED_CAPACITY, CREATED_BY, UPDATED_BY) values(1, 1, '黑A001', 30, 1, 1);

insert into POURING_PART_SPEC(TENANT_ID, ID, CREATED_BY, MIXTURE_ID, POURING_PART_ID, UNIT_PROJECT_ID, UPDATED_BY) values(1, 1, 1, 1, 1, 1, 1);

insert into POURING_PART(TENANT_ID, ID, NAME, PINYIN_ABBR_NAME, CREATED_BY, UPDATED_BY) values(1, 1, '五层以下楼板', 'wcyxlb', 1, 1);

insert into mixture(ID, type, code, GRADE_NAME) values(1, 'M', 'M25----','M2.5');
insert into mixture(ID, type, code, GRADE_NAME) values(2, 'M', 'M50----','M5.0');
insert into mixture(ID, type, code, GRADE_NAME) values(3, 'M', 'M75----','M7.5');
insert into mixture(ID, type, code, GRADE_NAME) values(4, 'M', 'M10----','M10');
insert into mixture(ID, type, code, GRADE_NAME) values(5, 'M', 'M15----','M15');
insert into mixture(ID, type, code, GRADE_NAME) values(6, 'M', 'M20----','M20');
insert into mixture(ID, type, code, GRADE_NAME) values(7, 'C', 'C10----','C10');
insert into mixture(ID, type, code, GRADE_NAME) values(8, 'C', 'C15----','C15');
insert into mixture(ID, type, code, GRADE_NAME) values(9, 'C', 'C20----','C20');
insert into mixture(ID, type, code, GRADE_NAME) values(10, 'C', 'C25----','C25');
insert into mixture(ID, type, code, GRADE_NAME) values(11, 'C', 'C30----','C30');
insert into mixture(ID, type, code, GRADE_NAME) values(12, 'C', 'C35----','C35');
insert into mixture(ID, type, code, GRADE_NAME) values(13, 'C', 'C40----','C40');
insert into mixture(ID, type, code, GRADE_NAME) values(14, 'C', 'C45----','C45');
insert into mixture(ID, type, code, GRADE_NAME) values(15, 'C', 'C50----','C50');
insert into mixture(ID, type, code, GRADE_NAME) values(16, 'C', 'C55----','C55');
insert into mixture(ID, type, code, GRADE_NAME) values(17, 'C', 'C60----','C60');
insert into mixture(ID, type, code, GRADE_NAME) values(18, 'C', 'C65----','C65');
insert into mixture(ID, type, code, GRADE_NAME) values(19, 'C', 'C70----','C70');
insert into mixture(ID, type, code, GRADE_NAME) values(20, 'C', 'C75----','C75');
insert into mixture(ID, type, code, GRADE_NAME) values(21, 'C', 'C80----','C80');
insert into mixture(ID, type, code, GRADE_NAME) values(22, 'C', 'C85----','C85');
insert into mixture(ID, type, code, GRADE_NAME) values(23, 'C', 'C90----','C90');
insert into mixture(ID, type, code, GRADE_NAME) values(24, 'C', 'C95----','C95');
insert into mixture(ID, type, code, GRADE_NAME) values(25, 'C', '100----','100');

insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(1, '------1','冬季施工费零上10度','djsgflssd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(2, '------2','冬季施工费零下10度','djsgflxsd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(3, '------C','超流体','clt');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(4, '------L','路面','lm');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(5, '------X','细骨料','xgl');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(6, '-----1-','防冻零下5度','fdlxwd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(7, '-----2-','防冻零下10度','fdlxsd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(8, '-----3-','防冻零下15度','fdlxswd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(9, '-----4-','防冻零下20度','fdlxesd');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(10, '----0--','防渗S10','fss10');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(11, '----2--','防渗S12','fss12');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(12, '----6--','防渗S6','fss6');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(13, '----8--','防渗S8','fss8');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(14, '----W--','微膨胀','wpz');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(15, '---C---','超早强','szq');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(16, '---H---','缓凝剂','hnj');
insert into additive(ID,CODE,NAME,PINYIN_ABBR_NAME) values(17, '---Z---','早强','zq');

insert into unit_project(TENANT_ID, ID, NAME, CITY_NAME, NUMBER, PROVINCE_NAME, STREET, ZIP_CODE, ALTITUDE, LATITUDE, LONGITUDE, CONTACT_ID, CONTRACT_ID, CREATED_BY, UPDATED_BY) values(1, 1, '绿波华园16号楼', '阿城', 253, '黑龙', '胜利大街', '153000', 0, 0, 0, 1, 1, 1, 1 );

insert into contract(TENANT_ID, ID, CREATED_BY, CUSTOMER_ID, UPDATED_BY) values(1, 1, 1, 1, 1);

insert into contact(TENANT_ID, ID, EMAIL, MALE, NAME, CREATED_BY, UPDATED_BY ) values(1, 1, 'wingspan@126.com', 0, '王三', 1, 1 );

SET FOREIGN_KEY_CHECKS=1;
