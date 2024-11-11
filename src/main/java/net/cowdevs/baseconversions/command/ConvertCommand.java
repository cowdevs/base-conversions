package net.cowdevs.baseconversions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.util.Formatting;
import java.math.BigInteger;

import static net.cowdevs.baseconversions.BaseConversions.CONFIG;
import static net.cowdevs.baseconversions.BaseConversions.NAME;

public class ConvertCommand {
    private static final int COLOR_CYAN = 0x55ffff;
    private static final int COLOR_GREEN = 0x55ff55;
    private static final String BASE_ERROR = "Bases must be between 2-36!";
    private static final String NUMBER_ERROR = "Invalid number format!";
    private static final String SIZE_ERROR = "Number is too large to handle!";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("base")
                .then(CommandManager.literal("convert")
                        .then(CommandManager.argument("number", StringArgumentType.string())
                                .then(CommandManager.argument("from", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("to", IntegerArgumentType.integer())
                                                .executes(ConvertCommand::run)
                                        )
                                )
                        )
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        String number = StringArgumentType.getString(context, "number");
        int from = IntegerArgumentType.getInteger(context, "from");
        int to = IntegerArgumentType.getInteger(context, "to");

        try {
            validateBase(from, to);
            validateNumber(number, from);
            validateSize(number, from);
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal(e.getMessage()));
            return 0;
        }

        String result = convert(number, from, to);
        MutableText resultText = createResultText(result, to);
        context.getSource().sendFeedback(() -> resultText, false);

        return 1;
    }

    private static void validateBase(int from, int to) {
        if (!(2 <= from && from <= 36 || 2 <= to && to <= 36)) {
            throw new IllegalArgumentException(BASE_ERROR);
        }
    }

    private static void validateNumber(String number, int from) {
        String[] parts = number.split("\\.");
        if (parts.length > 2) {
            throw new IllegalArgumentException(NUMBER_ERROR);
        }

        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, from);
        for (String part : parts) {
            for (char c : part.toCharArray()) {
                if (validChars.indexOf(c) == -1) {
                    throw new IllegalArgumentException(NUMBER_ERROR);
                }
            }
        }
    }

    private static void validateSize(String number, int from) {
        String[] parts = number.split("\\.");
        String integerPart = parts[0];

        BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
        BigInteger parsedNumber = new BigInteger(integerPart, from);

        if (parsedNumber.compareTo(maxLong) > 0) {
            throw new IllegalArgumentException(SIZE_ERROR);
        }
    }

    private static MutableText createResultText(String result, int to) {
        return Text.literal("[")
                .append(Text.literal(NAME).styled(style -> style
                        .withColor(COLOR_CYAN)
                        .withFormatting(Formatting.BOLD)))
                .append("] ")
                .append(Text.literal(result).styled(style -> style
                        .withColor(COLOR_GREEN)
                        .withFormatting(Formatting.UNDERLINE)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, result))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to Copy to Clipboard")))))
                .append(" (base" + to + ")");
    }

    public static String convert(String number, int from, int to) {
        String[] parts = number.split("\\.");
        String integerPart = parts[0];
        String fractionalPart = parts.length > 1 ? parts[1] : "";

        String convertedIntegerPart = convertIntegerPart(integerPart, from, to);
        String convertedFractionalPart = convertFractionalPart(fractionalPart, from, to);

        return convertedFractionalPart.isEmpty() ? convertedIntegerPart : convertedIntegerPart + "." + convertedFractionalPart;
    }

    private static String convertIntegerPart(String number, int from, int to) {
        long decimal = parseIntegerPart(number, from);
        return decimal == 0 ? "0" : convertFromDecimal(decimal, to);
    }

    private static long parseIntegerPart(String number, int base) {
        long decimal = 0;
        long p = 1;

        for (int i = number.length() - 1; i >= 0; i--) {
            char digitChar = number.charAt(i);
            int digitValue = Character.isDigit(digitChar) ?
                             digitChar - '0' :
                             Character.toUpperCase(digitChar) - 'A' + 10;
            decimal += digitValue * p;
            p *= base;
        }

        return decimal;
    }

    private static String convertFromDecimal(long decimal, int base) {
        StringBuilder convertedNumber = new StringBuilder();

        while (decimal > 0) {
            int remainder = (int) (decimal % base);
            char digitChar = (remainder < 10) ?
                             (char) (remainder + '0') :
                             (char) (remainder - 10 + 'A');
            convertedNumber.append(digitChar);
            decimal /= base;
        }

        return convertedNumber.reverse().toString();
    }

    private static String convertFractionalPart(String number, int from, int to) {
        double decimal = parseFractionalPart(number, from);
        return convertFromDecimalFraction(decimal, to);
    }

    private static double parseFractionalPart(String number, int base) {
        double decimal = 0.0;
        double p = 1.0 / base;

        for (int i = 0; i < number.length(); i++) {
            char digitChar = number.charAt(i);
            int digitValue = Character.isDigit(digitChar) ?
                             digitChar - '0' :
                             Character.toUpperCase(digitChar) - 'A' + 10;
            decimal += digitValue * p;
            p /= base;
        }

        return decimal;
    }

    private static String convertFromDecimalFraction(double decimal, int base) {
        StringBuilder convertedNumber = new StringBuilder();

            while (decimal > 0 && convertedNumber.length() < CONFIG.maxFractionLength()) {
            decimal *= base;
            int digitValue = (int) decimal;
            char digitChar = (digitValue < 10) ?
                             (char) (digitValue + '0') :
                             (char) (digitValue - 10 + 'A');
            convertedNumber.append(digitChar);
            decimal -= digitValue;
        }

        return convertedNumber.toString();
    }
}