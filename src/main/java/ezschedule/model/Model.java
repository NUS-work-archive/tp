package ezschedule.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Predicate;

import ezschedule.commons.core.GuiSettings;
import ezschedule.logic.commands.Command;
import ezschedule.model.event.Event;
import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {

    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Event> PREDICATE_SHOW_ALL_EVENTS = unused -> true;

    /**
     * {@code Predicate} that always evaluate to false
     */
    Predicate<Event> PREDICATE_SHOW_NO_EVENTS = unused -> false;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' scheduler file path.
     */
    Path getSchedulerFilePath();

    /**
     * Sets the user prefs' scheduler file path.
     */
    void setSchedulerFilePath(Path schedulerFilePath);

    /**
     * Returns the Scheduler.
     */
    ReadOnlyScheduler getScheduler();

    /**
     * Replaces scheduler data with the data in {@code scheduler}.
     */
    void setScheduler(ReadOnlyScheduler scheduler);

    /**
     * Returns true if an event with the same identity as {@code event} exists in the scheduler.
     */
    boolean hasEvent(Event event);

    /**
     * Returns true if another event exists at the same time as {@code event} in the scheduler.
     */
    boolean hasEventAtTime(Event event);


    /**
     * Returns true if another event exists (excluding {@code currentEvent})
     * at the same time as {@code event} in the scheduler.
     */
    boolean hasEventAtTime(Event currentEvent, Event eventToCheck);

    /**
     * Adds the given event.
     * {@code event} must not already exist in the scheduler.
     */
    void addEvent(Event event);

    /**
     * Deletes the given event.
     * {@code event} must exist in the scheduler.
     */
    void deleteEvent(Event event);

    /**
     * Replaces the given event {@code target} with {@code editedEvent}.
     * {@code target} must exist in the scheduler.
     * The event identity of {@code editedEvent} must not be the same as another existing Event in the scheduler.
     */
    void setEvent(Event target, Event editedEvent);

    /**
     * Adds the given event as recent event.
     */
    void addRecentEvent(Event event);
    /**
     * Adds the given command as most recently executed command
     */
    void addRecentCommand(Command command);

    /**
     * Clears the recent event.
     */
    void clearRecent();

    ArrayList<Command> recentCommands();

    ArrayList<Event> recentEvent();

    /**
     * Returns an unmodifiable view of the event list
     */
    ObservableList<Event> getEventList();

    /**
     * Returns an unmodifiable view of the filtered event list
     */
    ObservableList<Event> getFilteredEventList();

    /**
     * Returns an unmodifiable view of the upcoming event list
     */
    ObservableList<Event> getUpcomingEventList();

    /**
     * Returns an unmodifiable view of the find command event list
     */
    ObservableList<Event> getFindEventList();

    /**
     * Updates the filter of the filtered event list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredEventList(Predicate<Event> predicate);

    /**
     * Updates the filter of the upcoming event list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateUpcomingEventList(Predicate<Event> predicate);

    /**
     * Updates the filter of the find event list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFindEventList(Predicate<Event> predicate);
}
