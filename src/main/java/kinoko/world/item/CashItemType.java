package kinoko.world.item;

public enum CashItemType {
    HAIR_COUPON, // 1
    FACE_COUPON, // 2
    SKIN_COUPON, // 3
    STORE_PERMIT, // 4
    PET_REVIVE, // 5
    EMOTION, // 6
    SAFETY_CHARM, // 7
    PET, // 8
    EFFECT, // 9
    BULLET, // 10
    EMPLOYEE, // 11
    MEGAPHONE, // 12
    SUPER_MEGAPHONE, // 13, 45 (skull)
    ITEM_MEGAPHONE, // 14

    WEATHER, // 16
    PET_NAME_TAG, // 17
    MESSAGE_BALLOON, // 18
    MESO_SACK, // 19 (also maple point chips)
    JUKEBOX, // 20
    NOTE, // 21
    TELEPORT_ROCK, // 22
    AP_RESET, // 23
    SP_RESET, // 24
    ITEM_TAG, // 25
    ITEM_GUARD, // 26
    INCUBATOR, // 27
    PET_SKILL, // 28
    SHOP_SCANNER, // 29 (owl of minerva)
    PET_FOOD, // 30
    DELIVERY_TICKET, // 31
    AD_BOARD, // 32

    EFFECT_CONSUMABLE, // 34
    EYE_COLOR, // 35
    WEDDING_TICKET, // 36
    WEDDING_INVITATION, // 37
    TRAVELING_MERCHANT, // 38
    STORE_REMOTE_CONTROLLER, // 39
    GACHAPON, // 40
    MORPH, // 41
    PET_EVOL, // 42
    AVATAR_MESSENGER, // 43

    MAPLE_TV_MESSENGER, // 47, 48, 49, 50, 51, 52
    NAME_CHANGE, // 53
    CHARACTER_TRANSFER, // 54
    HAIR_MEMBERSHIP_COUPON, // 55

    PET_SNACK, // 58
    KEY, // 59
    REMOTE_GACHAPON, // 60
    TRIPLE_MEGAPHONE, // 61
    HOURGLASS, // 62
    REVIVE_ITEM, // 63
    KARMA_SCISSOR_ITEM, // 64
    ITEM_GUARD_PERMANENT, // 65
    CHARACTER_SLOT_COUPON, // 66
    GOLDEN_HAMMER_ITEM, // 67

    VEGA_SPELL, // 71
    REWARD_BOX, // 72
    SKILL_LEARN_ITEM, // 73
    MIRACLE_CUBE, // 74

    PAMS_SONG_ITEM, // 77
    REMOTE_QUEST_ITEM; // 78


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
                return EMPLOYEE;
            }
            case 504 -> {
                return TELEPORT_ROCK;
            }
            case 505 -> {
                if (itemId % 10 != 0) {
                    return SP_RESET;
                } else {
                    return AP_RESET;
                }
            }
            case 506 -> {
                if (itemId / 1000 == 5061) {
                    return ITEM_GUARD_PERMANENT;
                } else if (itemId / 1000 == 5062) {
                    return MIRACLE_CUBE;
                } else {
                    switch (itemId % 10) {
                        case 0 -> {
                            return ITEM_TAG;
                        }
                        case 1 -> {
                            return ITEM_GUARD;
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
                        return MEGAPHONE;
                    }
                    case 2, 4 -> {
                        return SUPER_MEGAPHONE;
                    }
                    case 5 -> {
                        return MAPLE_TV_MESSENGER;
                    }
                    case 6 -> {
                        return ITEM_MEGAPHONE;
                    }
                    case 7 -> {
                        return TRIPLE_MEGAPHONE;
                    }
                }
            }
            case 508 -> {
                return MESSAGE_BALLOON;
            }
            case 509 -> {
                return NOTE;
            }
            case 510 -> {
                return JUKEBOX;
            }
            case 512 -> {
                return WEATHER;
            }
            case 513 -> {
                return SAFETY_CHARM;
            }
            case 514 -> {
                return STORE_PERMIT;
            }
            case 515 -> {
                switch (itemId / 1000) {
                    case 5150, 5151, 5154 -> {
                        return HAIR_COUPON;
                    }
                    case 5152 -> {
                        if (itemId / 100 == 51520) {
                            return FACE_COUPON;
                        } else if (itemId / 100 == 51521) {
                            return EYE_COLOR;
                        }
                    }
                    case 5153 -> {
                        return SKIN_COUPON;
                    }
                }
            }
            case 516 -> {
                return EMOTION;
            }
            case 517 -> {
                if (itemId == 5170000) {
                    return PET_NAME_TAG;
                }
            }
            case 518 -> {
                return PET_REVIVE;
            }
            case 519 -> {
                return PET_SKILL;
            }
            case 520 -> {
                return MESO_SACK;
            }
            case 522 -> {
                return GACHAPON;
            }
            case 523 -> {
                return SHOP_SCANNER;
            }
            case 524 -> {
                return PET_FOOD;
            }
            case 525 -> {
                if (itemId == 5251100) {
                    return WEDDING_INVITATION;
                } else {
                    return WEDDING_TICKET;
                }
            }
            case 528 -> {
                if (itemId / 1000 == 5281) {
                    return EFFECT_CONSUMABLE;
                }
            }
            case 530 -> {
                return MORPH;
            }
            case 533 -> {
                return DELIVERY_TICKET;
            }
            case 537 -> {
                return AD_BOARD;
            }
            case 538 -> {
                return PET_EVOL;
            }
            case 539 -> {
                return AVATAR_MESSENGER;
            }
            case 540 -> {
                if (itemId / 1000 == 5400) {
                    return NAME_CHANGE;
                } else if (itemId / 1000 == 5401) {
                    return CHARACTER_TRANSFER;
                }
            }
            case 542 -> {
                if (itemId / 1000 == 5420) {
                    return HAIR_MEMBERSHIP_COUPON;
                }
            }
            case 543 -> {
                if (itemId / 1000 == 5430) {
                    return CHARACTER_SLOT_COUPON;
                }
            }
            case 545 -> {
                if (itemId / 1000 == 5451) {
                    return REMOTE_GACHAPON;
                } else {
                    return TRAVELING_MERCHANT;
                }
            }
            case 546 -> {
                return PET_SNACK;
            }
            case 547 -> {
                return STORE_REMOTE_CONTROLLER;
            }
            case 549 -> {
                return KEY;
            }
            case 550 -> {
                return HOURGLASS;
            }
            case 551 -> {
                return REVIVE_ITEM;
            }
            case 552 -> {
                return KARMA_SCISSOR_ITEM;
            }
            case 553 -> {
                return REWARD_BOX;
            }
            case 557 -> {
                return GOLDEN_HAMMER_ITEM;
            }
            case 561 -> {
                return VEGA_SPELL;
            }
            case 562 -> {
                return SKILL_LEARN_ITEM;
            }
            case 564 -> {
                return PAMS_SONG_ITEM;
            }
            case 566 -> {
                return REMOTE_QUEST_ITEM;
            }
        }
        return null;
    }
}
