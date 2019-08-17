package com.quetzalcoatl.minskinfobot.handlers.util;

import java.util.ArrayList;
import java.util.List;

public final class HandlerUtil {
    /**
     * Telegram API sets max length of message to this value. Sending longer answer leads to exception.
     */
    private static final int MAX_MESSAGE_LENGTH = 4096;
    private static final String DOUBLE_LF = "\n\n";

    private HandlerUtil() {
    }


    /**
     * Telegram API has a restriction for maximum size of message. If size is exceeds that maximum, API can throw an
     * exceptions. To resolve this situation method splits potential long messages for a parts. Each of this part
     * will be send to API separately.
     * @param entries List of formatted text. Each entry in this list - is a separate record. For example, record
     *                about one movie, or about one news.
     * @param prefix Is a text, that will be appended only to the first message in created message sequence.
     * @return List with one entry if result message don't exceed <code>MAX_MESSAGE_LENGTH</code>, otherwise
     * returns list with splitted messages.
     */
    static public List<String> splitMessages(List<String> entries, String prefix) {
        List<String> result = new ArrayList<>();
        boolean isFirstEntry = true;
        String stringItem = "";
        for (String entryText : entries) {
            int entryLength = entryText.length();
            if (stringItem.length() + entryLength + DOUBLE_LF.length() >= MAX_MESSAGE_LENGTH) {
                String replacement = isFirstEntry ? prefix : "";
                stringItem = stringItem.replaceFirst(DOUBLE_LF, replacement);
                isFirstEntry = false;
                result.add(stringItem);
                stringItem = String.join(DOUBLE_LF, "", entryText);
            } else {
                stringItem = String.join(DOUBLE_LF, stringItem, entryText);
            }
        }
        String replacement = isFirstEntry ? prefix : "";
        stringItem = stringItem.replaceFirst(DOUBLE_LF, replacement);
        result.add(stringItem);

        return result;
    }

}
