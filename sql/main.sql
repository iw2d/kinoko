DROP TABLE IF EXISTS character_data;
DROP TABLE IF EXISTS character_stat;
DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE character_stat
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255),
    gender   INT,
    skin     INT,
    face     INT,
    hair     INT,
    level    INT,
    job      INT,
    sub_job  INT,
    base_str INT,
    base_dex INT,
    base_int INT,
    base_luk INT,
    hp       INT,
    max_hp   INT,
    mp       INT,
    max_mp   INT,
    ap       INT,
    sp       INT[],
    exp      INT,
    pop      INT,
    money    INT,
    pos_map  INT,
    portal   INT
);

CREATE TABLE character_data
(
    id             SERIAL PRIMARY KEY,
    account_id     INT,
    character_stat INT,
    friend_max     INT,
    FOREIGN KEY (account_id) REFERENCES account (id),
    FOREIGN KEY (character_stat) REFERENCES character_stat (id)
);