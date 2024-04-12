package kinoko.world.item;

public enum CashItemType {
    NONE(0),
    HAIR(1),
    FACE(2),
    SKIN(3),
    SHOP(4),
    SET_PET_LIFE(5),
    EMOTION(6),
    PROTECT_ON_DIE(7),
    PET(8),
    EFFECT(9),
    BULLET(10),
    SHOP_EMPLOYEE(11),
    SPEAKER_CHANNEL(12),
    SPEAKER_WORLD(13),
    ITEM_SPEAKER(14),
    SPEAKER_BRIDGE(15),
    WEATHER(16),
    SET_PET_NAME(17),
    MESSAGEBOX(18),
    MONEY_POCKET(19),
    JUKEBOX(20),
    SEND_MEMO(21),
    MAP_TRANSFER(22),
    STAT_CHANGE(23),
    SKILL_CHANGE(24),
    NAMING(25),
    PROTECTING(26),
    INCUBATOR(27),
    PET_SKILL(28),
    SHOP_SCANNER(29),
    PET_FOOD(30),
    QUICK_DELIVERY(31),
    AD_BOARD(32),
    CONSUME_EFFECT_ITEM(33),
    CONSUME_ARE_A_BUFF_ITEM(34),
    COLOR_LENS(35),
    WEDDING_TICKET(36),
    INVITATION_TICKET(37),
    SELECT_NPC(38),
    REMOTE_SHOP(39),
    GACHAPON_COUPON(40),
    MORPH(41),
    PET_EVOL(42),
    AVATAR_MEGAPHONE(43),
    HEART_SPEAKER(44),
    SKULL_SPEAKER(45),
    REMOVABLE(46),
    MAPLE_TV(47),
    MAPLE_SOLE_TV(48),
    MAPLE_LOVE_TV(49),
    MEGA_TV(50),
    MEGA_SOLE_TV(51),
    MEGA_LOVE_TV(52),
    CHANGE_CHARACTER_NAME(53),
    TRANSFER_WORLD_COUPON(54),
    HAIR_SHOP_MEMBERSHIP_COUPON(55),
    FACE_SHOP_MEMBERSHIP_COUPON(56),
    SKIN_SHOP_MEMBERSHIP_COUPON(57),
    PET_SNACK(58),
    GACHAPON_BOX_MASTER_KEY(59),
    GACHAPON_REMOTE(60),
    ART_SPEAKER_WORLD(61),
    EXTEND_EXPIRE_DATE(62),
    UPGRADE_TOMB(63), // wheel of destiny
    KARMA_SCISSORS(64),
    EXPIRED_PROTECTING(65),
    CHARACTER_SALE(66),
    ITEM_UPGRADE(67),
    CASH_ITEM_GACHAPON(68),
    CASH_GACHAPON_OPEN(69),
    CHANGE_MAPLE_POINT(70),
    VEGA(71),
    REWARD(72),
    MASTERY_BOOK(73),
    CUBE_REVEAL(74),
    SKILL_RESET(75),
    DRAGON_BALL(76),
    RECOVER_UPGRADE_COUNT(77),
    QUEST_DELIVERY(78);

    private final int value;

    CashItemType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static CashItemType getByItemId(int itemId) {
        // get_cashslot_item_type
        switch (itemId / 10000) {
            case 500 -> {
                return PET;
            }
            case 501 -> {
                return EFFECT;
            }
            case 502 -> {
                return BULLET;
            }
            case 503 -> {
                return SHOP_EMPLOYEE;
            }
            case 504 -> {
                return MAP_TRANSFER;
            }
            case 505 -> {
                if (itemId % 10 != 0) {
                    return SKILL_CHANGE;
                } else {
                    return STAT_CHANGE;
                }
            }
            case 506 -> {
                if (itemId / 1000 == 5061) {
                    return EXPIRED_PROTECTING;
                } else if (itemId / 1000 == 5062) {
                    return CUBE_REVEAL;
                } else {
                    switch (itemId % 10) {
                        case 0 -> {
                            return NAMING;
                        }
                        case 1 -> {
                            return PROTECTING;
                        }
                        case 2, 3 -> {
                            return INCUBATOR;
                        }
                    }
                }
            }
            case 507 -> {
                switch (itemId % 10000 / 1000) {
                    case 1 -> {
                        return SPEAKER_CHANNEL;
                    }
                    case 2 -> {
                        return SPEAKER_WORLD;
                    }
                    case 4 -> {
                        return SKULL_SPEAKER;
                    }
                    case 5 -> {
                        switch (itemId % 10) {
                            case 0 -> {
                                return MAPLE_TV;
                            }
                            case 1 -> {
                                return MAPLE_SOLE_TV;
                            }
                            case 2 -> {
                                return MAPLE_LOVE_TV;
                            }
                            case 3 -> {
                                return MEGA_TV;
                            }
                            case 4 -> {
                                return MEGA_SOLE_TV;
                            }
                            case 5 -> {
                                return MEGA_LOVE_TV;
                            }
                        }
                    }
                    case 6 -> {
                        return ITEM_SPEAKER;
                    }
                    case 7 -> {
                        return ART_SPEAKER_WORLD;
                    }
                    case 8 -> {
                        return SPEAKER_BRIDGE;
                    }
                }
            }
            case 508 -> {
                return MESSAGEBOX;
            }
            case 509 -> {
                return SEND_MEMO;
            }
            case 510 -> {
                return JUKEBOX;
            }
            case 512 -> {
                return WEATHER;
            }
            case 513 -> {
                return PROTECT_ON_DIE;
            }
            case 514 -> {
                return SHOP;
            }
            case 515 -> {
                switch (itemId / 1000) {
                    case 5150, 5151, 5154 -> {
                        return HAIR;
                    }
                    case 5152 -> {
                        if (itemId / 100 == 51520) {
                            return FACE;
                        } else if (itemId / 100 == 51521) {
                            return COLOR_LENS;
                        }
                    }
                    case 5153 -> {
                        return SKIN;
                    }
                }
            }
            case 516 -> {
                return EMOTION;
            }
            case 517 -> {
                if (itemId == 5170000) {
                    return SET_PET_NAME;
                }
            }
            case 518 -> {
                return SET_PET_LIFE;
            }
            case 519 -> {
                return PET_SKILL;
            }
            case 520 -> {
                return MONEY_POCKET;
            }
            case 522 -> {
                return GACHAPON_COUPON;
            }
            case 523 -> {
                return SHOP_SCANNER;
            }
            case 524 -> {
                return PET_FOOD;
            }
            case 525 -> {
                if (itemId == 5251100) {
                    return INVITATION_TICKET;
                } else {
                    return WEDDING_TICKET;
                }
            }
            case 528 -> {
                if (itemId / 1000 == 5281) {
                    return CONSUME_EFFECT_ITEM;
                }
            }
            case 530 -> {
                return MORPH;
            }
            case 533 -> {
                return QUICK_DELIVERY;
            }
            case 537 -> {
                return AD_BOARD;
            }
            case 538 -> {
                return PET_EVOL;
            }
            case 539 -> {
                return AVATAR_MEGAPHONE;
            }
            case 540 -> {
                if (itemId / 1000 == 5400) {
                    return CHANGE_CHARACTER_NAME;
                } else if (itemId / 1000 == 5401) {
                    return TRANSFER_WORLD_COUPON;
                }
            }
            case 542 -> {
                if (itemId / 1000 == 5420) {
                    return HAIR_SHOP_MEMBERSHIP_COUPON;
                }
            }
            case 543 -> {
                if (itemId / 1000 == 5430) {
                    return CHARACTER_SALE;
                }
            }
            case 545 -> {
                if (itemId / 1000 == 5451) {
                    return GACHAPON_REMOTE;
                } else {
                    return SELECT_NPC;
                }
            }
            case 546 -> {
                return PET_SNACK;
            }
            case 547 -> {
                return REMOTE_SHOP;
            }
            case 549 -> {
                return GACHAPON_BOX_MASTER_KEY;
            }
            case 550 -> {
                return EXTEND_EXPIRE_DATE;
            }
            case 551 -> {
                return UPGRADE_TOMB;
            }
            case 552 -> {
                return KARMA_SCISSORS;
            }
            case 553 -> {
                return REWARD;
            }
            case 557 -> {
                return ITEM_UPGRADE;
            }
            case 561 -> {
                return VEGA;
            }
            case 562 -> {
                return MASTERY_BOOK;
            }
            case 564 -> {
                return RECOVER_UPGRADE_COUNT;
            }
            case 566 -> {
                return QUEST_DELIVERY;
            }
        }
        return NONE;
    }
}
