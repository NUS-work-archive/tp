package ezschedule.logic.commands;

import static ezschedule.commons.core.Messages.MESSAGE_EVENT_END_TIME_EARLIER_THAN_START_TIME;
import static ezschedule.commons.core.Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX;
import static ezschedule.logic.parser.CliSyntax.PREFIX_DATE;
import static ezschedule.logic.parser.CliSyntax.PREFIX_END;
import static ezschedule.logic.parser.CliSyntax.PREFIX_NAME;
import static ezschedule.logic.parser.CliSyntax.PREFIX_START;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import ezschedule.commons.core.index.Index;
import ezschedule.commons.util.CollectionUtil;
import ezschedule.logic.commands.exceptions.CommandException;
import ezschedule.model.Model;
import ezschedule.model.event.Date;
import ezschedule.model.event.Event;
import ezschedule.model.event.Name;
import ezschedule.model.event.Time;

/**
 * Edits the details of an existing {@code Event} in the {@code Scheduler}.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the event identified "
            + "by the index number used in the displayed event list. "
            + "\nExisting values will be overwritten by the input values."
            + "\nParameters: INDEX (must be a positive integer) "
            + PREFIX_NAME + "NAME "
            + PREFIX_DATE + "DATE "
            + PREFIX_START + "START TIME "
            + PREFIX_END + "END TIME "
            + "\nExample: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + "TENNIS "
            + PREFIX_DATE + "2023-20-12 "
            + PREFIX_START + "18:00 "
            + PREFIX_END + "20:00";

    public static final String MESSAGE_EDIT_EVENT_SUCCESS = "Edited Event: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in the scheduler.";
    public static final String MESSAGE_EVENT_EXIST_AT_TIME = "Another event already exists at the chosen time";

    private final Index index;
    private final EditEventDescriptor editEventDescriptor;

    /**
     * @param index               of the event in the filtered event list to edit
     * @param editEventDescriptor details to edit the event with
     */
    public EditCommand(Index index, EditEventDescriptor editEventDescriptor) {
        requireNonNull(index);
        requireNonNull(editEventDescriptor);

        this.index = index;
        this.editEventDescriptor = new EditEventDescriptor(editEventDescriptor);
    }

    /**
     * Creates and returns a {@code Event} with the details of {@code eventToEdit}
     * edited with {@code editEventDescriptor}.
     */
    private static Event createEditedEvent(Event eventToEdit, EditEventDescriptor editEventDescriptor) {
        assert eventToEdit != null;

        Name updatedName = editEventDescriptor.getName().orElse(eventToEdit.getName());
        Date updatedDate = editEventDescriptor.getDate().orElse(eventToEdit.getDate());
        Time updatedStartTime = editEventDescriptor.getStartTime().orElse(eventToEdit.getStartTime());
        Time updatedEndTime = editEventDescriptor.getEndTime().orElse(eventToEdit.getEndTime());

        return new Event(updatedName, updatedDate, updatedStartTime, updatedEndTime);
    }

    @Override
    public String commandWord() {
        return COMMAND_WORD;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(
                    MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, index.getZeroBased() + 1));
        }

        Event eventToEdit = lastShownList.get(index.getZeroBased());
        Event editedEvent = createEditedEvent(eventToEdit, editEventDescriptor);

        if (editedEvent.getEndTime().isBefore(editedEvent.getStartTime())) {
            throw new CommandException(MESSAGE_EVENT_END_TIME_EARLIER_THAN_START_TIME);
        } else if (!eventToEdit.equals(editedEvent) && model.hasEvent(editedEvent)) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        } else if (model.hasEventAtTime(eventToEdit, editedEvent)) {
            throw new CommandException(MESSAGE_EVENT_EXIST_AT_TIME);
        } else {
            model.clearRecent();
            model.addRecentCommand(this);
            model.setEvent(eventToEdit, editedEvent);
            model.updateFilteredEventList(Model.PREDICATE_SHOW_ALL_EVENTS);
            model.addRecentEvent(eventToEdit);
            model.addRecentEvent(editedEvent);
        }
        return new CommandResult(String.format(MESSAGE_EDIT_EVENT_SUCCESS, editedEvent));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editEventDescriptor.equals(e.editEventDescriptor);
    }

    /**
     * Stores the details to edit the event with.
     * Each non-empty field value will replace the corresponding field value of the event.
     */
    public static class EditEventDescriptor {

        private Name name;
        private Date date;
        private Time startTime;
        private Time endTime;

        public EditEventDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditEventDescriptor(EditEventDescriptor toCopy) {
            setName(toCopy.name);
            setDate(toCopy.date);
            setStartTime(toCopy.startTime);
            setEndTime(toCopy.endTime);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, date, startTime, endTime);
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Optional<Time> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        public void setStartTime(Time startTime) {
            this.startTime = startTime;
        }

        public Optional<Time> getEndTime() {
            return Optional.ofNullable(endTime);
        }

        public void setEndTime(Time endTime) {
            this.endTime = endTime;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditEventDescriptor)) {
                return false;
            }

            // state check
            EditEventDescriptor e = (EditEventDescriptor) other;

            return getName().equals(e.getName())
                    && getDate().equals(e.getDate())
                    && getStartTime().equals(e.getStartTime())
                    && getEndTime().equals(e.getEndTime());
        }
    }
}
