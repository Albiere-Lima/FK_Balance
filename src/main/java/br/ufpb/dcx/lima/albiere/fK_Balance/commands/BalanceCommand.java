package br.ufpb.dcx.lima.albiere.fK_Balance.commands;

import br.ufpb.dcx.lima.albiere.fK_Balance.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Balance.configs.FileConfigurationExtends;
import br.ufpb.dcx.lima.albiere.fK_Balance.exeptions.MessageCommandNotDefienedException;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.EconomiesManager;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("%economies")
public class BalanceCommand extends BaseCommand {

    private final FileConfigurationExtends messages = FK_Balance.getMessagesConfig();
    private final EconomiesManager manager = FK_Balance.getEconomiesManager();
    private String command;


    @Default
    @Subcommand("see|ver")
    public void commandSee(CommandSender sender, @Optional OnlinePlayer player) {
        command = getExecCommandLabel();
        if(player == null ) {
            try {
                String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".seeYouBalance");
                String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
                String prefix = manager.getEconomy(command).get().getPrefix();
                String finalMessage = messageTemplate
                        .replace("{Balance}", balanceString)
                        .replace("{PrefixBalance}", prefix);
                sender.sendMessage(finalMessage);
            } catch(Exception e) {
                throw new MessageCommandNotDefienedException("Comando: "+command+" está com configurações faltando! Por favor, verifique os arquivos: economies.yml, messages.yml");
            }
        }
        else {
            try {
                String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".seeOthersBalance");
                String playerName = player.getPlayer().getName();
                String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(player.getPlayer().getUniqueId())));
                String prefix = manager.getEconomy(command).get().getPrefix();
                String finalMessage = messageTemplate
                        .replace("{Player}", playerName)
                        .replace("{Balance}", balanceString)
                        .replace("{PrefixBalance}", prefix);
                sender.sendMessage(finalMessage);
            } catch(Exception e) {
                throw new MessageCommandNotDefienedException("Comando: "+command+" está com configurações faltando! Por favor, verifique os arquivos: economies.yml, messages.yml");
            }
        }

    }


    @Subcommand("pay")
    public void onPay(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        command = getExecCommandLabel();
        if (arg2 == null) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".invalidCommand");
            String finalMessage = messageTemplate
                    .replace("{Command}", "/"+command+" pay <Player> <value>");
            sender.sendMessage(finalMessage);
            return;

        } else if(!arg1.getPlayer().isOnline()) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".playerIsNotOnline");
            String finalMessage = messageTemplate
                    .replace("{Player}", arg1.getPlayer().getName());
            sender.sendMessage(finalMessage);
            return;
        }

        BigDecimal money;
        try {
            money = stringtoBigDecimal(arg2);
        } catch (NumberFormatException e) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberFormatException");
            sender.sendMessage(messageTemplate);
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberNegative");
            sender.sendMessage(messageTemplate);
            return;
        }
        if (sender == arg1.getPlayer()) {

            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".playerEquals");
            sender.sendMessage(messageTemplate);
            return;

        }

        if (manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId()).compareTo(money) < 0) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".lowerValue");
            sender.sendMessage(messageTemplate);
            return;
        }
        manager.getEconomy(command).get().withdraw(((Player) sender).getUniqueId(), money);
        manager.getEconomy(command).get().deposit(arg1.getPlayer().getUniqueId(), money);

        String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".paySuccess.Sender");
        sender.sendMessage(messageTemplate);

        messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".paySuccess.Player");
        String finalMessage = messageTemplate
                .replace("{Player}", arg1.getPlayer().getName())
                .replace("{Sender}", sender.getName())
                .replace("{Money}", manager.getEconomy(command).get().getPrefix() + " " + bigDecimaltoString(String.valueOf(money)));

        arg1.getPlayer().sendMessage(finalMessage);

    }


    @Subcommand("set")
    public void onSet(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        command = getExecCommandLabel();
        if (!sender.hasPermission("FK_Balance.set")) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".noPermission");
            sender.sendMessage(messageTemplate);

        }

        if (arg2 == null) {

            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".invalidCommand");
            sender.sendMessage(messageTemplate); return;

        }else if(!arg1.getPlayer().isOnline()) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".playerIsNotOnline");
            sender.sendMessage(messageTemplate);
            return;
        }

        BigDecimal money;
        try {

            money = stringtoBigDecimal(arg2);
        } catch (NumberFormatException e) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberFormatException");
            sender.sendMessage(messageTemplate);
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberNegative");
            sender.sendMessage(messageTemplate);
            return;

        }

        manager.getEconomy(command).get().setBalance(((Player) sender).getUniqueId(), money);
        if (arg1.getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".setSuccess.Sender");
            String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            String prefix = manager.getEconomy(command).get().getPrefix();
            String finalMessage = messageTemplate
                    .replace("{Balance}", prefix + " " + balanceString);
            sender.sendMessage(finalMessage);
        } else {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".setSuccess.PlayerY");
            String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            String prefix = manager.getEconomy(command).get().getPrefix();
            String finalMessage = messageTemplate
                    .replace("{Player}", arg1.getPlayer().getName())
                    .replace("{Balance}", prefix + " " + balanceString);
            sender.sendMessage(finalMessage);
            messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".setSuccess.Player");
            balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            prefix = manager.getEconomy(command).get().getPrefix();
            finalMessage = messageTemplate
                    .replace("{Sender}", sender.getName())
                    .replace("{Balance}", prefix + " " +balanceString);
            sender.sendMessage(finalMessage);
        }
    }

    @Subcommand("forcereload")
    public void onReload(CommandSender sender) {
        command = getExecCommandLabel();
        if(!sender.hasPermission("FK_Balance.forcereload")) return;
        FK_Balance.loadAllConfigs();
        sender.sendMessage(FK_Balance.getEconomies().getColouredString(command + ".prefix") + "Configurações atualizadas!");
    }


    @Subcommand("give")
    public void onGive(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        command = getExecCommandLabel();

        if (!sender.hasPermission("FK_Balance.give")) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".noPermission");
            sender.sendMessage(messageTemplate);

        }

        if (arg2 == null) {

            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".invalidCommand");
            sender.sendMessage(messageTemplate); return;

        }else if(!arg1.getPlayer().isOnline()) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".playerIsNotOnline");
            sender.sendMessage(messageTemplate);
            return;
        }

        BigDecimal money;
        try {

            money = stringtoBigDecimal(arg2);
        } catch (NumberFormatException e) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberFormatException");
            sender.sendMessage(messageTemplate);
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".numberNegative");
            sender.sendMessage(messageTemplate);
            return;

        }

        manager.getEconomy(command).get().deposit(arg1.getPlayer().getUniqueId(), money);
        if (Objects.requireNonNull(arg1).getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".giveSuccess.Sender");
            String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            String prefix = manager.getEconomy(command).get().getPrefix();
            String finalMessage = messageTemplate
                    .replace("{Balance}", prefix + " " + balanceString);
            sender.sendMessage(finalMessage);
        } else {
            String messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".giveSucess.PlayerY");
            String balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            String prefix = manager.getEconomy(command).get().getPrefix();
            String finalMessage = messageTemplate
                    .replace("{Player}", arg1.getPlayer().getName())
                    .replace("{Balance}", prefix + " " + balanceString);
            sender.sendMessage(finalMessage);
            messageTemplate = FK_Balance.getEconomies().getColouredString(command + ".prefix") + messages.getColouredString(command + ".giveSuccess.Player");
            balanceString = bigDecimaltoString(String.valueOf(manager.getEconomy(command).get().getBalance(((Player) sender).getUniqueId())));
            prefix = manager.getEconomy(command).get().getPrefix();
            finalMessage = messageTemplate
                    .replace("{Sender}", sender.getName())
                    .replace("{Balance}", prefix + " " + balanceString);
            sender.sendMessage(finalMessage);
        }
    }
    public BigDecimal stringtoBigDecimal(String s) {
        Pattern CURRENCY_PATTERN = Pattern.compile(
                "^(\\d+(\\.\\d+)?)(K|M|B|T|Q|QI|S)?$",
                Pattern.CASE_INSENSITIVE
        );

        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("A string não pode ser nula ou vazia.");
        }

        Matcher matcher = CURRENCY_PATTERN.matcher(s.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato inválido: \"" + s + "\".");
        }

        String numberPart = matcher.group(1);
        String suffixPart = matcher.group(3);

        BigDecimal value = new BigDecimal(numberPart);

        if (suffixPart != null) {
            switch (suffixPart.toUpperCase()) {
                case "K":
                    value = value.multiply(new BigDecimal("1000"));
                    break;
                case "M":
                    value = value.multiply(new BigDecimal("1000000"));
                    break;
                case "B":
                    value = value.multiply(new BigDecimal("1000000000"));
                    break;
                case "T":
                    value = value.multiply(new BigDecimal("1000000000000"));
                    break;
                case "Q":
                    value = value.multiply(new BigDecimal("1000000000000000"));
                    break;
                case "QI":
                    value = value.multiply(new BigDecimal("1000000000000000000"));
                    break;
                case "S":
                    value = value.multiply(new BigDecimal("1000000000000000000000"));
                    break;
            }
        }

        return value;
    }

    private static final BigDecimal KILO = new BigDecimal("1000");
    private static final BigDecimal MILHAO = new BigDecimal("1000000");
    private static final BigDecimal BILHAO = new BigDecimal("1000000000");
    private static final BigDecimal TRILHAO = new BigDecimal("1000000000000");
    private static final BigDecimal QUATRILHAO = new BigDecimal("1000000000000000");
    private static final BigDecimal QUINTILHAO = new BigDecimal("1000000000000000000");
    private static final BigDecimal SEXTILHAO = new BigDecimal("1000000000000000000000");

    public static String bigDecimaltoString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "0";
        }

        BigDecimal numberValue;
        try {
            numberValue = new BigDecimal(value);
        } catch (NumberFormatException e) {
            return "0";
        }

        if (numberValue.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        String sign = numberValue.compareTo(BigDecimal.ZERO) < 0 ? "-" : "";
        BigDecimal absValue = numberValue.abs();

        if (absValue.compareTo(SEXTILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, SEXTILHAO, "S");
        }
        if (absValue.compareTo(QUINTILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, QUINTILHAO, "QI");
        }
        if (absValue.compareTo(QUATRILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, QUATRILHAO, "Q");
        }
        if (absValue.compareTo(TRILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, TRILHAO, "T");
        }
        if (absValue.compareTo(BILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, BILHAO, "B");
        }
        if (absValue.compareTo(MILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, MILHAO, "M");
        }
        if (absValue.compareTo(KILO) >= 0) {
            return sign + formatWithSuffix(absValue, KILO, "K");
        }

        return numberValue.stripTrailingZeros().toPlainString();
    }

    private static String formatWithSuffix(BigDecimal value, BigDecimal divisor, String suffix) {
        BigDecimal result = value.divide(divisor, 2, RoundingMode.HALF_UP);
        return result + suffix;
    }
}
