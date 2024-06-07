package kinoko.world.item;

public enum CashItemType {
    // CIT
    NONE(0),
    HAIR(1),
    FACE(2),
    SKIN(3),
    SHOP(4),
    SETPETLIFE(5),
    EMOTION(6),
    PROTECTONDIE(7),
    PET(8),
    EFFECT(9),
    BULLET(10),
    SHOPEMPLOYEE(11),
    SPEAKERCHANNEL(12),
    SPEAKERWORLD(13),
    ITEMSPEAKER(14),
    SPEAKERBRIDGE(15),
    WEATHER(16),
    SETPETNAME(17),
    MESSAGEBOX(18),
    MONEYPOCKET(19),
    JUKEBOX(20),
    SENDMEMO(21),
    MAPTRANSFER(22),
    STATCHANGE(23),
    SKILLCHANGE(24),
    NAMING(25),
    PROTECTING(26),
    INCUBATOR(27),
    PETSKILL(28),
    SHOPSCANNER(29),
    PETFOOD(30),
    QUICKDELIVERY(31),
    ADBOARD(32),
    CONSUMEEFFECTITEM(33),
    CONSUMEAREABUFFITEM(34),
    COLORLENS(35),
    WEDDINGTICKET(36),
    INVITATIONTICKET(37),
    SELECTNPC(38),
    REMOTESHOP(39),
    GACHAPONCOUPON(40),
    MORPH(41),
    PETEVOL(42),
    AVATARMEGAPHONE(43),
    HEARTSPEAKER(44),
    SKULLSPEAKER(45),
    REMOVABLE(46),
    MAPLETV(47),
    MAPLESOLETV(48),
    MAPLELOVETV(49),
    MEGATV(50),
    MEGASOLETV(51),
    MEGALOVETV(52),
    CHANGECHARACTERNAME(53),
    TRANSFERWORLDCOUPON(54),
    HAIRSHOPMEMBERSHIPCOUPON(55),
    FACESHOPMEMBERSHIPCOUPON(56),
    SKINSHOPMEMBERSHIPCOUPON(57),
    PETSNACK(58),
    GACHAPONBOX_MASTERKEY(59),
    GACHAPONREMOTE(60),
    ARTSPEAKERWORLD(61),
    EXTENDEXPIREDATE(62),
    UPGRADETOMB(63), // wheel of destiny
    KARMASCISSORS(64),
    EXPIREDPROTECTING(65),
    CHARACTERSALE(66),
    ITEMUPGRADE(67),
    CASHITEMGACHAPON(68),
    CASHGACHAPONOPEN(69),
    CHANGEMAPLEPOINT(70),
    VEGA(71),
    REWARD(72),
    MASTERYBOOK(73),
    ITEM_UNRELEASE(74),
    SKILLRESET(75),
    DRAGONBALL(76),
    RECOVERUPGRADECOUNT(77),
    QUESTDELIVERY(78);

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
                return SHOPEMPLOYEE;
            }
            case 504 -> {
                return MAPTRANSFER;
            }
            case 505 -> {
                if (itemId % 10 != 0) {
                    return SKILLCHANGE;
                } else {
                    return STATCHANGE;
                }
            }
            case 506 -> {
                if (itemId / 1000 == 5061) {
                    return EXPIREDPROTECTING;
                } else if (itemId / 1000 == 5062) {
                    return ITEM_UNRELEASE;
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
                        return SPEAKERCHANNEL;
                    }
                    case 2 -> {
                        return SPEAKERWORLD;
                    }
                    case 4 -> {
                        return SKULLSPEAKER;
                    }
                    case 5 -> {
                        switch (itemId % 10) {
                            case 0 -> {
                                return MAPLETV;
                            }
                            case 1 -> {
                                return MAPLESOLETV;
                            }
                            case 2 -> {
                                return MAPLELOVETV;
                            }
                            case 3 -> {
                                return MEGATV;
                            }
                            case 4 -> {
                                return MEGASOLETV;
                            }
                            case 5 -> {
                                return MEGALOVETV;
                            }
                        }
                    }
                    case 6 -> {
                        return ITEMSPEAKER;
                    }
                    case 7 -> {
                        return ARTSPEAKERWORLD;
                    }
                    case 8 -> {
                        return SPEAKERBRIDGE;
                    }
                }
            }
            case 508 -> {
                return MESSAGEBOX;
            }
            case 509 -> {
                return SENDMEMO;
            }
            case 510 -> {
                return JUKEBOX;
            }
            case 512 -> {
                return WEATHER;
            }
            case 513 -> {
                return PROTECTONDIE;
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
                            return COLORLENS;
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
                    return SETPETNAME;
                }
            }
            case 518 -> {
                return SETPETLIFE;
            }
            case 519 -> {
                return PETSKILL;
            }
            case 520 -> {
                return MONEYPOCKET;
            }
            case 522 -> {
                return GACHAPONCOUPON;
            }
            case 523 -> {
                return SHOPSCANNER;
            }
            case 524 -> {
                return PETFOOD;
            }
            case 525 -> {
                if (itemId == 5251100) {
                    return INVITATIONTICKET;
                } else {
                    return WEDDINGTICKET;
                }
            }
            case 528 -> {
                if (itemId / 1000 == 5281) {
                    return CONSUMEEFFECTITEM;
                }
            }
            case 530 -> {
                return MORPH;
            }
            case 533 -> {
                return QUICKDELIVERY;
            }
            case 537 -> {
                return ADBOARD;
            }
            case 538 -> {
                return PETEVOL;
            }
            case 539 -> {
                return AVATARMEGAPHONE;
            }
            case 540 -> {
                if (itemId / 1000 == 5400) {
                    return CHANGECHARACTERNAME;
                } else if (itemId / 1000 == 5401) {
                    return TRANSFERWORLDCOUPON;
                }
            }
            case 542 -> {
                if (itemId / 1000 == 5420) {
                    return HAIRSHOPMEMBERSHIPCOUPON;
                }
            }
            case 543 -> {
                if (itemId / 1000 == 5430) {
                    return CHARACTERSALE;
                }
            }
            case 545 -> {
                if (itemId / 1000 == 5451) {
                    return GACHAPONREMOTE;
                } else {
                    return SELECTNPC;
                }
            }
            case 546 -> {
                return PETSNACK;
            }
            case 547 -> {
                return REMOTESHOP;
            }
            case 549 -> {
                return GACHAPONBOX_MASTERKEY;
            }
            case 550 -> {
                return EXTENDEXPIREDATE;
            }
            case 551 -> {
                return UPGRADETOMB;
            }
            case 552 -> {
                return KARMASCISSORS;
            }
            case 553 -> {
                return REWARD;
            }
            case 557 -> {
                return ITEMUPGRADE;
            }
            case 561 -> {
                return VEGA;
            }
            case 562 -> {
                return MASTERYBOOK;
            }
            case 564 -> {
                return RECOVERUPGRADECOUNT;
            }
            case 566 -> {
                return QUESTDELIVERY;
            }
        }
        return NONE;
    }
}
