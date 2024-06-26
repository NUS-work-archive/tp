package ezschedule.logic.commands;

import ezschedule.logic.commands.exceptions.CommandException;
import ezschedule.model.Model;

/**
 * Represents a {@code Command} with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;
    public abstract String commandWord();
}
