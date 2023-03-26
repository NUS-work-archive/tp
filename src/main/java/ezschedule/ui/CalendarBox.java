package ezschedule.ui;

import java.util.List;

import ezschedule.model.event.Event;
import ezschedule.model.event.EventMatchesDatePredicate;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * A UI component that displays information of {@code Event} in the calendar.
 */
public class CalendarBox extends UiPart<Region> {

    private static final String FXML = "CalendarBox.fxml";

    private List<Event> events;
    private Calendar.FilterExecutor filterExecutor;

    @FXML
    private StackPane calendarBoxPane;
    @FXML
    private Text calendarDate;
    @FXML
    private VBox calendarEvents;
    @FXML
    private Rectangle calendarHighlight;

    /**
     * Creates an empty {@code CalenderBox}.
     */
    public CalendarBox() {
        super(FXML);
    }

    /**
     * Creates a {@code CalenderBox} with the given {@code List<Event>} and date to display.
     */
    public CalendarBox(boolean isToday, String date, List<Event> events, Calendar.FilterExecutor filterExecutor) {
        super(FXML);
        this.events = events;
        this.filterExecutor = filterExecutor;
        setDate(date);
        setHighlightForToday(isToday);
        setEvents();
    }

    /**
     * List all events for the clicked date.
     */
    @FXML
    public void handleListEvents() {
        if (events != null) {
            filterExecutor.updateFilteredEventList(new EventMatchesDatePredicate(events.get(0).getDate()));
        }
    }

    private void setDate(String date) {
        calendarDate.setText(date);
    }

    private void setHighlightForToday(boolean isToday) {
        if (isToday) {
            calendarHighlight.setStroke(Color.RED);
        }
    }

    private void setEvents() {
        if (events != null) {
            calendarEvents.setStyle("-fx-background-color:GRAY");
            for (int i = 0; i < events.size(); i++) {
                if (i >= 1) {
                    Text moreEvents = new Text("...");
                    calendarEvents.getChildren().add(moreEvents);
                    break;
                }
                String eventName = getEventName(events.get(i));
                Text event = new Text(eventName);
                calendarEvents.getChildren().add(event);
            }
        }
    }

    private String getEventName(Event event) {
        String name = event.getName().toString();
        if (name.length() > 5) {
            String firstFiveChars = name.substring(0, 5);
            name = firstFiveChars + "...";
        }
        return name;
    }
}
