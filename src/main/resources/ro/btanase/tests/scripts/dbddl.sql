CREATE TABLE PUBLIC.CHORD (
  C_ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY,
  C_NAME VARCHAR(50),
  C_FILENAME VARCHAR(255),
  C_FILENAME2 VARCHAR(255),
  C_FILENAME3 VARCHAR(255),
  C_FILENAME4 VARCHAR(255),
  C_FILENAME5 VARCHAR(255)
);

CREATE TABLE PUBLIC.EXERCISE_SCORE (
  ES_ID INTEGER NOT NULL,
  C_ID INTEGER,
  S_ID INTEGER,
  CORRECT BOOLEAN,
  PRIMARY KEY (ES_ID)
);

CREATE TABLE PUBLIC.LESSON (
  L_ID INTEGER NOT NULL,
  L_NAME VARCHAR(50),
  L_NO_QUESTIONS INTEGER,
  L_TYPE VARCHAR(20) DEFAULT 'SINGLE',
  L_NO_CHORDS INTEGER,
  L_CHORD_DELAY INTEGER,
  L_ORDER INTEGER DEFAULT 0,
  PRIMARY KEY (L_ID)
);

CREATE TABLE PUBLIC.LESSON_CHORD (
  LC_ID INTEGER NOT NULL,
  C_ID INTEGER,
  L_ID INTEGER,
  PRIMARY KEY (LC_ID)
);

CREATE TABLE PUBLIC.SCORE (
  S_ID INTEGER NOT NULL,
  S_DATE TIMESTAMP,
  L_ID INTEGER,
  PRIMARY KEY (S_ID)
);

CREATE TABLE PUBLIC.SETTINGS (
  S_ID INTEGER NOT NULL,
  S_KEY VARCHAR(20),
  S_VALUE VARCHAR(255),
  PRIMARY KEY (S_ID)
);

ALTER TABLE PUBLIC.EXERCISE_SCORE
  ADD FOREIGN KEY (C_ID) 
  REFERENCES CHORD (C_ID);

ALTER TABLE PUBLIC.EXERCISE_SCORE
  ADD FOREIGN KEY (S_ID) 
  REFERENCES SCORE (S_ID);



ALTER TABLE PUBLIC.LESSON_CHORD
  ADD FOREIGN KEY (C_ID) 
  REFERENCES CHORD (C_ID);

ALTER TABLE PUBLIC.LESSON_CHORD
  ADD FOREIGN KEY (L_ID) 
  REFERENCES LESSON (L_ID);



ALTER TABLE PUBLIC.SCORE
  ADD FOREIGN KEY (L_ID) 
  REFERENCES LESSON (L_ID);



CREATE INDEX PUBLIC.SYS_IDX_10036 ON PUBLIC.LESSON_CHORD (L_ID);

CREATE INDEX PUBLIC.SYS_IDX_10038 ON PUBLIC.LESSON_CHORD (C_ID);

CREATE INDEX PUBLIC.SYS_IDX_10043 ON PUBLIC.SCORE (L_ID);

CREATE INDEX PUBLIC.SYS_IDX_10048 ON PUBLIC.EXERCISE_SCORE (C_ID);

CREATE INDEX PUBLIC.SYS_IDX_10050 ON PUBLIC.EXERCISE_SCORE (S_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10027_10028 ON PUBLIC.CHORD (C_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10030_10031 ON PUBLIC.LESSON (L_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10034_10035 ON PUBLIC.LESSON_CHORD (LC_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10041_10042 ON PUBLIC.SCORE (S_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10046_10047 ON PUBLIC.EXERCISE_SCORE (ES_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_SYS_PK_10053_10054 ON PUBLIC.SETTINGS (S_ID);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_UNI_KEY_10055 ON PUBLIC.SETTINGS (S_KEY);

CREATE UNIQUE INDEX PUBLIC.SYS_IDX_UNI_ORDER_10032 ON PUBLIC.LESSON (L_ORDER);

