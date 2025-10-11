BEGIN TRANSACTION;

------------------------------------------
--------------SCHEMAS---------------------
------------------------------------------
CREATE SCHEMA IF NOT EXISTS account;
CREATE SCHEMA IF NOT EXISTS player;
CREATE SCHEMA IF NOT EXISTS guild;
CREATE SCHEMA IF NOT EXISTS friend;
CREATE SCHEMA IF NOT EXISTS gift;
CREATE SCHEMA IF NOT EXISTS memo;
CREATE SCHEMA IF NOT EXISTS item;


------------------------------------------
--------------FUNCTIONS-------------------
------------------------------------------
CREATE OR REPLACE FUNCTION public.utc_now()
RETURNS timestamp without time zone
LANGUAGE sql
AS $function$
    SELECT now() AT TIME ZONE 'UTC';
$function$;

------------------------------------------
--------------ITEM TABLES-----------------
------------------------------------------
CREATE TABLE item.items (
    item_sn BIGSERIAL PRIMARY KEY,  -- auto-increment unique ID for every item instance
    item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    attribute SMALLINT DEFAULT 0,
    title TEXT DEFAULT '',
    date_expire TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_items_item_id
    ON item.items(item_id);

CREATE INDEX IF NOT EXISTS idx_items_date_expire
    ON item.items(date_expire);

CREATE TABLE IF NOT EXISTS item.equip_data (
    item_sn BIGINT PRIMARY KEY REFERENCES item.items(item_sn) ON DELETE CASCADE,  -- unique for every equip item instance
    inc_str SMALLINT DEFAULT 0,
    inc_dex SMALLINT DEFAULT 0,
    inc_int SMALLINT DEFAULT 0,
    inc_luk SMALLINT DEFAULT 0,
    inc_max_hp SMALLINT DEFAULT 0,
    inc_max_mp SMALLINT DEFAULT 0,
    inc_pad SMALLINT DEFAULT 0,
    inc_mad SMALLINT DEFAULT 0,
    inc_pdd SMALLINT DEFAULT 0,
    inc_mdd SMALLINT DEFAULT 0,
    inc_acc SMALLINT DEFAULT 0,
    inc_eva SMALLINT DEFAULT 0,
    inc_craft SMALLINT DEFAULT 0,
    inc_speed SMALLINT DEFAULT 0,
    inc_jump SMALLINT DEFAULT 0,
    ruc SMALLINT DEFAULT 0,
    cuc SMALLINT DEFAULT 0,
    iuc INT DEFAULT 0,
    chuc SMALLINT DEFAULT 0,
    grade SMALLINT DEFAULT 0,
    option_1 SMALLINT DEFAULT 0,
    option_2 SMALLINT DEFAULT 0,
    option_3 SMALLINT DEFAULT 0,
    socket_1 SMALLINT DEFAULT 0,
    socket_2 SMALLINT DEFAULT 0,
    level_up_type SMALLINT DEFAULT 0,
    level SMALLINT DEFAULT 0,
    exp INT DEFAULT 0,
    durability INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_equip_data_item_sn
    ON item.equip_data(item_sn);


CREATE TABLE IF NOT EXISTS item.pet_data (
    item_sn BIGINT PRIMARY KEY REFERENCES item.items(item_sn) ON DELETE CASCADE,
    pet_name TEXT,
    level SMALLINT DEFAULT 0,
    fullness SMALLINT DEFAULT 0,
    tameness SMALLINT DEFAULT 0,
    pet_skill SMALLINT DEFAULT 0,
    pet_attribute SMALLINT DEFAULT 0,
    remain_life INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_pet_data_item_sn
    ON item.pet_data(item_sn);


CREATE TABLE IF NOT EXISTS item.ring_data (
    item_sn BIGINT PRIMARY KEY REFERENCES item.items(item_sn) ON DELETE CASCADE,
    pair_character_id INT,
    pair_character_name TEXT,
    pair_item_sn BIGINT
);

CREATE INDEX IF NOT EXISTS idx_ring_data_item_sn
    ON item.ring_data(item_sn);


------------------------------------------
--------------ACCOUNT TABLES--------------
------------------------------------------

CREATE TABLE IF NOT EXISTS account.accounts (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    secondary_password TEXT,
    character_slots INT NOT NULL DEFAULT 3,
    nx_credit INT NOT NULL DEFAULT 0,
    nx_prepaid INT NOT NULL DEFAULT 0,
    maple_point INT NOT NULL DEFAULT 0,
    trunk_size INT NOT NULL DEFAULT 24,
    trunk_money INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS account.trunk_item (
    account_id INT NOT NULL REFERENCES account.accounts(id) ON DELETE CASCADE,
    item_sn BIGINT NOT NULL REFERENCES item.items(item_sn) ON DELETE CASCADE,
    slot INT NOT NULL,
    PRIMARY KEY (account_id, slot)
);

CREATE INDEX IF NOT EXISTS idx_trunk_item_sn
    ON account.trunk_item(item_sn);

CREATE INDEX IF NOT EXISTS idx_trunk_item_account_item
    ON account.trunk_item(account_id, item_sn);


CREATE TABLE IF NOT EXISTS account.locker_item (
    account_id INT NOT NULL REFERENCES account.accounts(id) ON DELETE CASCADE,
    item_sn BIGINT NOT NULL REFERENCES item.items(item_sn) ON DELETE CASCADE,
    slot INT NOT NULL,
    commodity_id INT,
    PRIMARY KEY (account_id, slot)
);

CREATE INDEX IF NOT EXISTS idx_locker_item_sn
    ON account.locker_item(item_sn);

CREATE INDEX IF NOT EXISTS idx_locker_item_account_item
    ON account.locker_item(account_id, item_sn);


CREATE TABLE IF NOT EXISTS account.wishlist (
    account_id INT NOT NULL REFERENCES account.accounts(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL,
    slot INT NOT NULL,
    PRIMARY KEY (account_id, slot)
);

CREATE INDEX IF NOT EXISTS idx_wishlist_item_id
    ON account.wishlist(item_id);

CREATE INDEX IF NOT EXISTS idx_wishlist_account_item
    ON account.wishlist(account_id, item_id);


------------------------------------------
-------------PLAYER TABLES----------------
------------------------------------------

CREATE TABLE IF NOT EXISTS player.characters (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account.accounts(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    money INT NOT NULL DEFAULT 0,
    ext_slot_expire TIMESTAMP,
    friend_max INT NOT NULL DEFAULT 100,
    party_id INT,
    guild_id INT,
    creation_time TIMESTAMP NOT NULL DEFAULT UTC_NOW(),
    max_level_time TIMESTAMP,
    online BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS player.stats (
    character_id INT PRIMARY KEY REFERENCES player.characters(id) ON DELETE CASCADE,
    gender SMALLINT NOT NULL,
    skin SMALLINT NOT NULL,
    face INT NOT NULL,
    hair INT NOT NULL,
    level SMALLINT NOT NULL DEFAULT 1,
    job SMALLINT NOT NULL DEFAULT 0,
    sub_job SMALLINT NOT NULL DEFAULT 0,
    base_str SMALLINT NOT NULL DEFAULT 4,
    base_dex SMALLINT NOT NULL DEFAULT 4,
    base_int SMALLINT NOT NULL DEFAULT 4,
    base_luk SMALLINT NOT NULL DEFAULT 4,
    hp INT NOT NULL DEFAULT 50,
    max_hp INT NOT NULL DEFAULT 50,
    mp INT NOT NULL DEFAULT 50,
    max_mp INT NOT NULL DEFAULT 50,
    ap SMALLINT NOT NULL DEFAULT 0,
    exp INT NOT NULL DEFAULT 0,
    pop SMALLINT NOT NULL DEFAULT 0,
    pos_map INT NOT NULL DEFAULT 0,
    portal SMALLINT NOT NULL DEFAULT 0,
    pet_1 BIGINT,
    pet_2 BIGINT,
    pet_3 BIGINT
);

CREATE TABLE IF NOT EXISTS player.skill_points (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    skill_id INT NOT NULL,
    points INT NOT NULL DEFAULT 0,
    PRIMARY KEY (character_id, skill_id)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'inventory_type_enum') THEN
        CREATE TYPE inventory_type_enum AS ENUM (
            'EQUIPPED',
            'EQUIP',
            'CONSUME',
            'INSTALL',
            'ETC',
            'CASH'
        );
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS player.inventory (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    item_sn BIGINT NOT NULL REFERENCES item.items(item_sn) ON DELETE CASCADE,
    inventory_type inventory_type_enum NOT NULL,
    slot INT NOT NULL,
    PRIMARY KEY (character_id, item_sn)
);

CREATE INDEX IF NOT EXISTS idx_inventory_item_sn
    ON player.inventory(item_sn);

CREATE INDEX IF NOT EXISTS idx_inventory_char_item
    ON player.inventory(character_id, item_sn);


CREATE TABLE IF NOT EXISTS player.skill_cooltime (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    skill_id INT NOT NULL,
    cooldown_end TIMESTAMP NOT NULL,
    PRIMARY KEY (character_id, skill_id)
);


CREATE TABLE IF NOT EXISTS player.skill_record (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    skill_id INT NOT NULL,
    level INT NOT NULL,
    master_level INT,
    PRIMARY KEY (character_id, skill_id)
);

CREATE TABLE IF NOT EXISTS player.quest_record (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    quest_id INT NOT NULL,
    status INT NOT NULL,
    progress TEXT,
    completed_time TIMESTAMP,
    PRIMARY KEY (character_id, quest_id)
);

CREATE TABLE IF NOT EXISTS player.config (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    pet_consume_item INT NOT NULL DEFAULT 0,
    pet_consume_mp_item INT NOT NULL DEFAULT 0,
    pet_exception_list INT[] NOT NULL DEFAULT '{}',
    func_key_types SMALLINT[] NOT NULL DEFAULT '{}',
    func_key_ids INT[] NOT NULL DEFAULT '{}',
    quickslot_key_map INT[] NOT NULL DEFAULT '{}',
    PRIMARY KEY (character_id)
);

CREATE TABLE IF NOT EXISTS player.character_macro (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    macro_index INT NOT NULL,            -- index/order of the macro
    name TEXT NOT NULL,
    mute BOOLEAN NOT NULL DEFAULT FALSE,
    skills INT[] NOT NULL,               -- size: GameConstants.MACRO_SKILL_COUNT
    PRIMARY KEY (character_id, macro_index)
);

CREATE TABLE IF NOT EXISTS player.popularity (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    other_character_id INT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (character_id, other_character_id)
);

CREATE TABLE IF NOT EXISTS player.minigame (
    character_id INT PRIMARY KEY REFERENCES player.characters(id) ON DELETE CASCADE,
    omok_wins INT NOT NULL DEFAULT 0,
    omok_ties INT NOT NULL DEFAULT 0,
    omok_losses INT NOT NULL DEFAULT 0,
    omok_score DOUBLE PRECISION NOT NULL DEFAULT 0,
    memory_wins INT NOT NULL DEFAULT 0,
    memory_ties INT NOT NULL DEFAULT 0,
    memory_losses INT NOT NULL DEFAULT 0,
    memory_score DOUBLE PRECISION NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS player.map_transfer (
    character_id INT PRIMARY KEY REFERENCES player.characters(id) ON DELETE CASCADE,
    map_id INT NOT NULL,
    old_map_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS player.wild_hunter (
    character_id INT PRIMARY KEY REFERENCES player.characters(id) ON DELETE CASCADE,
    riding_type INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS player.wild_hunter_mob (
    character_id INT REFERENCES player.characters(id) ON DELETE CASCADE,
    mob_id INT NOT NULL,
    PRIMARY KEY (character_id, mob_id)
);

CREATE TABLE IF NOT EXISTS player.config (
    character_id INT PRIMARY KEY REFERENCES player.characters(id) ON DELETE CASCADE,
    config_key TEXT NOT NULL,
    config_value TEXT
);


------------------------------------------
--------------FRIEND TABLES---------------
------------------------------------------

CREATE TABLE IF NOT EXISTS friend.friends (
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    friend_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    friend_name TEXT NOT NULL,
    friend_group TEXT,
    friend_status INT NOT NULL DEFAULT 0,
    PRIMARY KEY (character_id, friend_id)
);

CREATE INDEX IF NOT EXISTS idx_friend_friend_id
    ON friend.friends(friend_id);


------------------------------------------
---------------GIFT TABLES----------------
------------------------------------------

CREATE TABLE IF NOT EXISTS gift.gifts (
    item_sn BIGINT NOT NULL REFERENCES item.items(item_sn) ON DELETE CASCADE,
    receiver_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    commodity_id INT,
    sender_id INT,
    sender_name TEXT,
    sender_message TEXT,
    PRIMARY KEY (item_sn)
);

CREATE INDEX IF NOT EXISTS idx_gift_item_sn
    ON gift.gifts(item_sn);


CREATE INDEX IF NOT EXISTS idx_gift_receiver
    ON gift.gifts(receiver_id);

CREATE INDEX IF NOT EXISTS idx_gift_receiver_item
    ON gift.gifts(receiver_id, item_sn);


------------------------------------------
---------------GUILD TABLES---------------
------------------------------------------

CREATE TABLE IF NOT EXISTS guild.guilds (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    member_max INT NOT NULL DEFAULT 50,
    mark_bg SMALLINT,
    mark_bg_color SMALLINT,
    mark SMALLINT,
    mark_color SMALLINT,
    notice TEXT,
    points INT NOT NULL DEFAULT 0,
    level SMALLINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS guild.grade (
    guild_id INT NOT NULL REFERENCES guild.guilds(id) ON DELETE CASCADE,
    grade_index INT NOT NULL,
    grade_name TEXT NOT NULL,
    PRIMARY KEY (guild_id, grade_index)
);

CREATE TABLE IF NOT EXISTS guild.member (
    guild_id INT NOT NULL REFERENCES guild.guilds(id) ON DELETE CASCADE,
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    grade SMALLINT NOT NULL,
    join_date TIMESTAMP NOT NULL DEFAULT UTC_NOW(),
    last_login TIMESTAMP,
    PRIMARY KEY (character_id)
);


CREATE TABLE IF NOT EXISTS guild.board_entry (
    id INT NOT NULL,
    guild_id INT NOT NULL REFERENCES guild.guilds(id) ON DELETE CASCADE,
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    title TEXT,
    message TEXT NOT NULL,
    emoticon INT,
    timestamp TIMESTAMP NOT NULL DEFAULT UTC_NOW(),
    notice boolean DEFAULT false,
    PRIMARY KEY (guild_id, id)
);

-- enforce only one TRUE notice per guild
CREATE UNIQUE INDEX IF NOT EXISTS unique_guild_notice
ON guild.board_entry(guild_id)
WHERE notice = TRUE;

CREATE TABLE IF NOT EXISTS guild.board_entry_comment (
    id SERIAL PRIMARY KEY,
    entry_id INT NOT NULL,
    guild_id INT NOT NULL REFERENCES guild.guilds(id) ON DELETE CASCADE,
    character_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT UTC_NOW()
);


------------------------------------------
---------------MEMO TABLES----------------
------------------------------------------

CREATE TABLE IF NOT EXISTS memo.memo (
    id SERIAL PRIMARY KEY,
    receiver_id INT NOT NULL REFERENCES player.characters(id) ON DELETE CASCADE,
    memo_type INT NOT NULL,
    memo_content TEXT NOT NULL,
    sender_name TEXT,
    date_sent TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_memo_receiver
    ON memo.memo(receiver_id);


------------------------------------------
---------------CREATE VIEWS---------------
------------------------------------------
CREATE OR REPLACE VIEW item.full_item AS
SELECT
    i.item_sn,
    i.item_id,
    i.quantity,
    i.attribute,
    i.title,
    i.date_expire,
    e.inc_str, e.inc_dex, e.inc_int, e.inc_luk,
    e.inc_max_hp, e.inc_max_mp, e.inc_pad, e.inc_mad,
    e.inc_pdd, e.inc_mdd, e.inc_acc, e.inc_eva,
    e.inc_craft, e.inc_speed, e.inc_jump, e.ruc,
    e.cuc, e.iuc, e.chuc, e.grade, e.option_1, e.option_2, e.option_3,
    e.socket_1, e.socket_2, e.level_up_type, e.level, e.exp, e.durability,
    p.pet_name, p.level AS pet_level, p.fullness, p.tameness, p.pet_skill, p.pet_attribute, p.remain_life,
    r.pair_character_id, r.pair_character_name, r.pair_item_sn
FROM item.items i
LEFT JOIN item.equip_data e ON i.item_sn = e.item_sn
LEFT JOIN item.pet_data p ON i.item_sn = p.item_sn
LEFT JOIN item.ring_data r ON i.item_sn = r.item_sn;


------------------------------------------
---------------ON CREATION----------------
------------------------------------------

INSERT INTO account.accounts (username, password, secondary_password, character_slots, nx_credit, nx_prepaid, maple_point, trunk_size, trunk_money)
VALUES (
    'admin',
    '$2a$10$LGtpvyti5yVVdWxN8L5sH.UiioiRweGw84mFaWJlfasSFDJ8.QPaW', -- bcrypt hash of "admin"
    NULL,
    3,
    0,
    0,
    0,
    24,
    0
);

COMMIT TRANSACTION;