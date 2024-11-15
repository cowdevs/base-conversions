package net.cowdevs.logicalconversions.command;

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

import static net.cowdevs.logicalconversions.LogicalConversions.CONFIG;
import static net.cowdevs.logicalconversions.LogicalConversions.NAME;

public class BaseCommand {
    private static final int COLOR_CYAN = 0x55ffff;
    private static final int COLOR_GREEN = 0x55ff55;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("base")
                .then(CommandManager.literal("convert")
                        .then(CommandManager.argument("number", StringArgumentType.string())
                                .then(CommandManager.argument("from", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("to", IntegerArgumentType.integer())
                                                .executes(BaseCommand::run)
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
        if (!(2 <= from && from <= 36 && 2 <= to && to <= 36)) {
            throw new IllegalArgumentException("Bases must be between 2-36!");
        }
    }

    private static void validateNumber(String number, int from) {
        String[] parts = number.split("\\.");
        if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid number format!");
        }

        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, from);
        for (String part : parts) {
            for (char c : part.toCharArray()) {
                if (validChars.indexOf(c) == -1) {
                    throw new IllegalArgumentException("Invalid number format!");
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
            throw new IllegalArgumentException("Number is too large to handle!");
        }
    }

    private static MutableText createResultText(String result, int base) {
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
                .append(" (base" + base + ")");
    }

    public static String convert(String number, int from, int to) {
        String[] parts = number.split("\\.");
        String integerPart = parts[0];
        String fractionalPart = parts.length > 1 ? parts[1] : "";

        String convertedIntegerPart = convertInt(integerPart, from, to);
        String convertedFractionalPart = convertFrac(fractionalPart, from, to);

        return convertedFractionalPart.isEmpty() ? convertedIntegerPart : convertedIntegerPart + "." + convertedFractionalPart;
    }

    private static String convertInt(String input, int b1, int b2) {
        long d = 0;
        long p = 1;

        for (int i = input.length() - 1; i >= 0; i--) {
            char digit = input.charAt(i);
            int value = Character.isDigit(digit) ?
                    digit - '0' :
                    Character.toUpperCase(digit) - 'A' + 10;
            d += value * p;
            p *= b1;
        }

        StringBuilder output = new StringBuilder();

        while (d > 0) {
            int value = (int) (d % b2);
            char digit = (value < 10) ?
                    (char) (value + '0') :
                    (char) (value - 10 + 'A');
            output.append(digit);
            d /= b2;
        }

        return output.reverse().toString();
    }

    private static String convertFrac(String input, int b1, int b2) {
        double d = 0.0;
        double p = 1.0 / b1;

        for (int i = 0; i < input.length(); i++) {
            char digit = input.charAt(i);
            int value = Character.isDigit(digit) ?
                    digit - '0' :
                    Character.toUpperCase(digit) - 'A' + 10;
            d += value * p;
            p /= b1;
        }

        StringBuilder output = new StringBuilder();

        while (d > 0 && output.length() < CONFIG.maxFractionLength()) {
            d *= b2;
            int value = (int) d;
            char digit = (value < 10) ?
                    (char) (value + '0') :
                    (char) (value - 10 + 'A');
            output.append(digit);
            d -= value;
        }

        return output.toString();
    }
}