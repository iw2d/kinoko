package kinoko.server.user;

/**
 * Represents the various server responses for administrative actions.
 * The values correspond to the cases in the CField::OnAdminResult function.
 */
public enum AdminResultType {
    /**
     * Response for a successful character block. (e.g., /block command)
     * Message: "You have successfully blocked access."
     */
    BLOCK_SUCCESS(4),

    /**
     * Response for a successful character unblock.
     * Message: "The unblocking has been successful."
     */
    UNBLOCK_SUCCESS(5),

    /**
     * Response after attempting to remove a character from rankings.
     * Can be either success or failure (invalid name).
     */
    RANK_REMOVE_RESPONSE(6),

    /**
     * Handles admin chat messages or notices sent to the player.
     */
    ADMIN_CHAT(11),

    /**
     * Sets the character's visibility status (hide/show).
     * A value of 1 hides the character, 0 shows them.
     */
    SET_HIDE_STATUS(18),

    /**
     * Response for a hired merchant search, providing the location.
     */
    FIND_HIRED_MERCHANT_RESPONSE(21),

    /**
     * Forces the client to reload the mini-map.
     */
    RELOAD_MINIMAP(40),

    /**
     * Toggles the mini-map display off.
     */
    TOGGLE_MINIMAP(41),

    /**
     * A generic response indicating a request has failed.
     * Message: "Your request failed."
     */
    REQUEST_FAILED(42),

    /**
     * Response after sending a warning to a user (success or failure).
     */
    WARN_RESPONSE(43),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_1(51),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_2(52),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_3(53),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_4(54),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_5(55),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_6(56),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_7(57),

    /**
     * Displays a decoded string in the chat log (style 12).
     */
    DISPLAY_MESSAGE_8(58),

    /**
     * Displays a decoded string in the chat log (style 12).
     */
    DISPLAY_MESSAGE_9(71),

    /**
     * Displays a decoded string in the chat log (style 11).
     */
    DISPLAY_MESSAGE_10(72);


    private final int value;

    AdminResultType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AdminResultType getByValue(int value) {
        for (AdminResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}