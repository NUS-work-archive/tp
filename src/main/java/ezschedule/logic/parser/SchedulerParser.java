package ezschedule.logic.parser;

import static ezschedule.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static ezschedule.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ezschedule.logic.commands.AddCommand;
import ezschedule.logic.commands.ClearCommand;
import ezschedule.logic.commands.Command;
import ezschedule.logic.commands.DeleteCommand;
import ezschedule.logic.commands.EditCommand;
import ezschedule.logic.commands.ExitCommand;
import ezschedule.logic.commands.FindCommand;
import ezschedule.logic.commands.HelpCommand;
import ezschedule.logic.commands.ListCommand;
import ezschedule.logic.commands.RecurCommand;
import ezschedule.logic.commands.ShowNextCommand;
import ezschedule.logic.commands.UndoCommand;
import ezschedule.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class SchedulerParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case RecurCommand.COMMAND_WORD:
            return new RecurCommandParser().parse(arguments);

        case ShowNextCommand.COMMAND_WORD:
            return new ShowNextCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case UndoCommand.COMMAND_WORD:
            if (!arguments.equals("")) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            } else {
                return new UndoCommand();
            }

        case ListCommand.COMMAND_WORD:
            if (!arguments.equals("")) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            } else {
                return new ListCommand();
            }

        case ClearCommand.COMMAND_WORD:
            if (!arguments.equals("")) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            } else {
                return new ClearCommand();
            }

        case ExitCommand.COMMAND_WORD:
            if (!arguments.equals("")) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            } else {
                return new ExitCommand();
            }

        case HelpCommand.COMMAND_WORD:
            if (!arguments.equals("")) {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            } else {
                return new HelpCommand();
            }

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
