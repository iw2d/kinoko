DROP TABLE IF EXISTS wild_hunter_info;
DROP TABLE IF EXISTS quest_record;
DROP TABLE IF EXISTS skill_record;
DROP TABLE IF EXISTS character_data;
DROP TABLE IF EXISTS character_inventory;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS inventory;
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
    pos_map  INT,
    portal   INT
);

CREATE TABLE inventory
(
    id   SERIAL PRIMARY KEY,
    size INT
);

CREATE TABLE item
(
    sn              BIGSERIAL PRIMARY KEY,
    inventory_id    INT,
    inventory_index INT,
    item_id         INT,
    item_type       INT,
    cash            BOOLEAN,
    quantity        INT,
    attribute       INT,
    title           VARCHAR(255),
    equip_info      HSTORE,
    pet_info        HSTORE,
    FOREIGN KEY (inventory_id) REFERENCES inventory (id)
);

CREATE TABLE character_inventory
(
    id                SERIAL PRIMARY KEY,
    equipped          INT,
    equip_inventory   INT,
    consume_inventory INT,
    install_inventory INT,
    etc_inventory     INT,
    cash_inventory    INT,
    money             INT,
    FOREIGN KEY (equipped) REFERENCES inventory (id),
    FOREIGN KEY (equip_inventory) REFERENCES inventory (id),
    FOREIGN KEY (consume_inventory) REFERENCES inventory (id),
    FOREIGN KEY (install_inventory) REFERENCES inventory (id),
    FOREIGN KEY (etc_inventory) REFERENCES inventory (id),
    FOREIGN KEY (cash_inventory) REFERENCES inventory (id)
);

CREATE TABLE character_data
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255) UNIQUE,
    account_id          INT,
    character_stat      INT,
    character_inventory INT,
    friend_max          INT,
    FOREIGN KEY (account_id) REFERENCES account (id),
    FOREIGN KEY (character_stat) REFERENCES character_stat (id)
);

CREATE TABLE skill_record
(
    character_id     INT,
    skill_id         INT,
    skill_level      INT,
    master_level     INT,
    next_usable_time TIMESTAMP,
    PRIMARY KEY (character_id, skill_id),
    FOREIGN KEY (character_id) references character_data (id)
);

CREATE TABLE quest_record
(
    character_id   INT,
    quest_id       INT,
    quest_info     VARCHAR(255),
    quest_type     INT,
    completed_time TIMESTAMP,
    PRIMARY KEY (character_id, quest_id),
    FOREIGN KEY (character_id) references character_data (id)
);

CREATE TABLE wild_hunter_info
(
    character_id  INT,
    riding_type   INT,
    captured_mobs INT[],
    PRIMARY KEY (character_id),
    FOREIGN KEY (character_id) references character_data (id)
);