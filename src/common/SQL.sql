--Table 생성 쿼리문들

DROP TABLE register;
DROP TABLE open;
DROP TABLE lecture;
DROP TABLE student;
DROP TABLE professor;

CREATE TABLE lecture(
l_code varchar2(20) primary key
, l_subject varchar2(200) not null
, l_major varchar2(20) not null
, l_type varchar2(20) not null
, l_grade varchar2(1) not null
, l_maxstudent number(5) not null
, l_nowstudent number(5) not null
, l_time varchar2(10) not null
, l_room varchar2(20) not null
, l_time_index varchar2(1)
, l_module varchar2(1)
, l_credit varchar2(1)
, l_plan varchar2(3500)
);


CREATE TABLE professor(
p_regno varchar2(20) primary key
, p_password varchar2(20) default '0000'
, p_name varchar2(200) not null
, p_major varchar2(200) not null
);

CREATE TABLE student(
s_regno varchar2(20) primary key
, s_password varchar2(20) default '0000'
, s_name varchar2(200) not null
, s_major varchar2(200) not null
, s_grade varchar2(20) not null
, s_nowcredit number(5) default 0
);

CREATE TABLE REGISTER(
s_regno varchar2(20) REFERENCES STUDENT
, l_code varchar2(20) REFERENCES LECTURE
);

CREATE TABLE OPEN(
p_regno varchar2(20) REFERENCES PROFESSOR
, l_code varchar2(20) REFERENCES LECTURE
);

commit;
--학생,교수 생성 쿼리문

INSERT INTO student VALUES ('S1', '0000', '김성훈', '경영학과', '4', 0);
INSERT INTO student VALUES ('S2', '0000', '김부연', '일어일문학과', '4', 0);
INSERT INTO student VALUES ('S3', '0000', '이상직', '컴퓨터공학과', '3', 0);
INSERT INTO student VALUES ('S4', '0000', '남우현', '간호학과', '1', 0);
INSERT INTO student VALUES ('S5', '0000', '노다빈', '일어일문학과', '2', 0);
INSERT INTO student VALUES ('S6', '0000', '이영경', '컴퓨터공학과', '4', 0);

INSERT INTO professor VALUES ('P1', '0000', '혼또니', '컴퓨터공학과');
INSERT INTO professor VALUES ('P2', '0000', '지현수', '일어일문학과');
INSERT INTO professor VALUES ('P3', '0000', '우사미', '경영학과');
INSERT INTO professor VALUES ('P4', '0000', '최승호', '간호학과');

INSERT INTO lecture VALUES ('혼컴072755',   '컴퓨터의 원리',   '컴퓨터공학과',   '전공',   '1',   50,   0,   '화-1,2,3',   '101',   '2',   '1',   '3', '컴퓨터란 바로 이런 것이다');
INSERT INTO lecture VALUES ('혼힘072815',   '술과 코딩',   '컴퓨터공학과',   '전공',   '1',   100,   0,   '금-7,8,9',   '202',   '5',   '7',   '3', '술을 마셔야 코딩이 잘된다');
INSERT INTO lecture VALUES ('지일072906',   '일본어 첫걸음',   '일어일문학과',   '교양',   '1',   10,   0,   '화-4,5,6',   '201',   '2',   '4',   '3', '일본어는 첫걸음이 중요합니다');
INSERT INTO lecture VALUES ('혼자072725',   '자바는 혼또니 어려워',   '컴퓨터공학과',   '전공',   '1',   10,   0,   '월-1,2,3',   '201',   '1',   '1',   '3', '자바와 혼또니 어렵다 데스네');
INSERT INTO lecture VALUES ('지실072926',   '실무일본어2',   '일어일문학과',   '전공',   '3',   30,   0,   '수-1,2,3',   '101',   '3',   '1',   '3', '실무에서는 일본어를 이렇게 사용할 수 있습니다');
INSERT INTO lecture VALUES ('지내073017',   '내가 일본어를 가르치다니',   '일어일문학과',   '교양',   '4',   10,   0,   '목-4,5,6',   '103',   '4',   '4',   '3', '제가 일본어를 가르쳐도 될까요?');
INSERT INTO lecture VALUES ('혼나072741',   '나는 1기다',   '컴퓨터공학과',   '전공',   '1'   ,10,   0,   '화-7,8,9',   '302',   '2',   '7',   '3', '내가 몇기라고?');
INSERT INTO lecture VALUES ('지일073039',   '일본어는 예술이다',   '일어일문학과',   '전공',   '4'   ,100,   0,   '수-7,8,9',   '101',   '3',   '7',   '3', '예술과도 같은 일본어의 세계로 안내합니다');
INSERT INTO lecture VALUES ('우경073112',   '경영학원론',   '경영학과',   '전공',   '1',   100,   0,   '월-1,2,3',   '301',   '1',   '1',   '3', '경영학을 일본어로 배워봐요');
INSERT INTO lecture VALUES ('우인073124',   '인생경영',   '경영학과',   '전공',   '4',   50,   0,   '월-4,5,6',   '202',   '1',   '4',   '3', '인생을 어떻게 경영할 것인가');
INSERT INTO lecture VALUES ('우경073143',   '경영을 경영하다',   '경영학과',   '전공',   '4',   30,   0,   '수-4,5,6',   '201',   '3',   '4',   '3', '경영을 경영하는 방법을 배우는 수업');
INSERT INTO lecture VALUES ('최간073220',   '간호사의 마음가짐',   '간호학과',   '교양',   '1',   100,   0,   '화-4,5,6',   '101',   '2',   '4',   '3', '간호사는 딱 두줄로 끝나요');
INSERT INTO lecture VALUES ('최하074407',   '하고 계세요',   '간호학과',   '전공',   '4',   10,   0,   '금-1,2,3',   '201',   '5',   '1',   '3', 'while(true){잠깐} 하고 계세요');
INSERT INTO lecture VALUES ('최간074459',   '간호는 사랑이다',   '간호학과',   '교양',   '2',   30,   0,   '목-7,8,9',   '101',   '4',   '7',   '3', '사랑으로 하는 간호');

INSERT INTO open VALUES('P1',   '혼컴072755');
INSERT INTO open VALUES('P1',   '혼힘072815');
INSERT INTO open VALUES('P2',   '지일072906');
INSERT INTO open VALUES('P1',   '혼자072725');
INSERT INTO open VALUES('P2',   '지실072926');
INSERT INTO open VALUES('P2',   '지내073017');
INSERT INTO open VALUES('P1',   '혼나072741');
INSERT INTO open VALUES('P2',   '지일073039');
INSERT INTO open VALUES('P3',   '우경073112');
INSERT INTO open VALUES('P3',   '우인073124');
INSERT INTO open VALUES('P3',   '우경073143');
INSERT INTO open VALUES('P4',   '최간073220');
INSERT INTO open VALUES('P4',   '최하074407');
INSERT INTO open VALUES('P4',   '최간074459');

commit;