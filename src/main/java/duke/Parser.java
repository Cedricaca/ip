package duke;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Parser class coordinates the ui, tasklist and storage to interpret user input
 * @author Cedric
 */
public class Parser {
    private final TaskList taskList = new TaskList();
    private final Storage storage = new Storage();
    /**
     * Constructor for parser checks for the storage file/folder existence then moves information to tasklist
     */
    public Parser() {
        storage.move(taskList);
    }
    private final Pattern pFind = Pattern.compile("find");
    private final Pattern pMark = Pattern.compile("mark (\\d+)");
    private final Pattern pUnmark = Pattern.compile("unmark (\\d+)");
    private final Pattern pTodo = Pattern.compile("todo");
    private final Pattern pDeadline = Pattern.compile("deadline");
    private final Pattern pEvent = Pattern.compile("event");
    private final Pattern pBy = Pattern.compile("/by");
    private final Pattern pFrom = Pattern.compile("/from");
    private final Pattern pTo = Pattern.compile("/to");
    private final Pattern pDelete = Pattern.compile("delete (\\d+)");
    private final Pattern pUpdate = Pattern.compile("update (\\d+)");
    /**
     * Receives input from Ui then interprets it before calling functions from tasklist, ui and storage
     */
    public String interpret(String input) {

        Matcher mMark = pMark.matcher(input);
        Matcher mUnmark = pUnmark.matcher(input);
        Matcher mTodo = pTodo.matcher(input);
        Matcher mDeadline = pDeadline.matcher(input);
        Matcher mEvent = pEvent.matcher(input);
        Matcher mBy = pBy.matcher(input);
        Matcher mFrom = pFrom.matcher(input);
        Matcher mTo = pTo.matcher(input);
        Matcher mDelete = pDelete.matcher(input);
        Matcher mFind = pFind.matcher(input);
        Matcher mUpdate = pUpdate.matcher(input);

        if (input.equals("reset")) {
            taskList.clear();
            storage.clear();
            return "List cleared!";
        } else if (input.equals("bye")) {
            return "Bye. Hope to see you again soon!";
        } else if (input.equals("list")) {
            return taskList.getList();
        } else if (mUpdate.find()) {
            return update(input);
        } else if (mFind.find()) {
            String newInput = input.replace("find", "").trim();
            ArrayList<Task> tasks = taskList.find(newInput);
            return find(tasks);
        } else if (mDelete.find()) {
            String captured = mDelete.group(1);
            return delete(captured);
        } else if (mUnmark.find()) {
            String captured = mUnmark.group(1);
            return unmark(captured);
        } else if (mMark.find()) {
            String captured = mMark.group(1);
            return mark(captured);
        } else if (mTodo.find()) {
            String newInput = input.replace("todo", "");
            return todo(newInput);
        } else if (mEvent.find()) {
            if (mFrom.find() && mTo.find()) {
                return event(input);
            } else {
                return "pls input your start and end of the event.";
            }
        } else if (mDeadline.find()) {
            if (mBy.find()) {
                return deadline(input);
            } else {
                return "please include a deadline";
            }
        } else {
            return "Sorry, no idea what u talking about lulz";
        }

    }
    private String delete(String captured) {
        int number = Integer.parseInt(captured);
        if (number > 0 && number <= taskList.getLength()) {
            Task t = taskList.delete(number - 1);
            storage.delete(number-1);

            String result = "OK! I have deleted this task:\n";
            result = result + t.toString();
            return result;
        } else {
            return "Please input a valid number.";
        }

    }

    private String unmark(String captured) {
        int number = Integer.parseInt(captured);
        if (number > 0 && number <= taskList.getLength()) {
            Task t = taskList.get(number - 1);
            t.unmark();
            storage.edit(number - 1, t.export());

            String result = "Oh no! I have marked this as not done:\n";
            result = result + t;
            return result;
        } else {
            return "Please input a valid number.";
        }
    }

    private String mark(String captured) {
        int number = Integer.parseInt(captured);
        if (number > 0 && number <= taskList.getLength()) {
            Task t = taskList.get(number - 1);
            t.mark();
            storage.edit(number - 1, t.export());

            String result = "Nice! I have marked this as done:\n";
            result = result + t;
            return result;
        } else {
            return "Please input a valid number.";
        }
    }

    private String todo(String newInput) {
        Todo n = new Todo(newInput, false);
        if (newInput.trim().equals("")) {
            return "Task cannot be empty!";
        } else {
            taskList.add(n);
            storage.add(n.export());

            String result = "OK, I have added this task :\n";
            result = result + n + "\n";
            result = result + "You now have " + taskList.getLength() + " items in the list.";
            return result;
        }
    }

    private String event(String input) {
        int startIndex = input.indexOf("/from");
        int startIndexTo = input.indexOf("/to");

        String subFrom = input.substring(startIndex + 5, startIndexTo).trim();
        String subTo = input.substring(startIndexTo + 3).trim();
        String newInput = input.substring(input.indexOf("event") + 5, startIndex);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime ldt;
        LocalDateTime ldt2;
        try {
            ldt = LocalDateTime.parse(subFrom, formatter);
            ldt2 = LocalDateTime.parse(subTo, formatter);
        } catch (DateTimeParseException e) {
            // Handle parsing exceptions
            return "Please enter a valid date/time";
        }
        if (newInput.trim().equals("")) {
            return "Task cannot be empty!";
        } else {
            Event n = new Event(newInput, false, ldt, ldt2);
            taskList.add(n);
            storage.add(n.export());

            String result = "OK, I have added this task :\n";
            result = result + n + "\n";
            result = result + "You now have " + taskList.getLength() + " items in the list.";
            return result;
        }
    }

    private String deadline(String input) {
        int finalIndex = input.indexOf("/by") + 3;
        String dL = input.substring(finalIndex);
        String newInput = input.substring(input.indexOf("deadline") + 8, input.indexOf("/by"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MM-dd HH:mm");
        LocalDateTime ldt;
        try {
            // Parse the string into a LocalDate object
            ldt = LocalDateTime.parse(dL, formatter);
        } catch (DateTimeParseException e) {
            return "Please enter a valid date/time";
        }
        if (newInput.trim().equals("")) {
            return "Task cannot be empty!";
        } else {
            Deadline n = new Deadline(newInput, false, ldt);
            taskList.add(n);
            String result = "OK, I have added this task :\n";
            result = result + n + "\n";
            result = result + "You now have " + taskList.getLength() + " items in the list.";
            storage.add(n.export());
            return result;
        }
    }

    private String find(ArrayList<Task> tasks) {
        if (tasks.size() == 0) {
            return "There are no tasks that match your description!";
        } else {
            String result = "These are the tasks that I found: \n";
            for (Task t : tasks) {
                result = result + t + "\n";
            }
            return result;
        }
    }
    private String update(String input) {
        Matcher mTodo = pTodo.matcher(input);
        Matcher mDeadline = pDeadline.matcher(input);
        Matcher mEvent = pEvent.matcher(input);
        Matcher mBy = pBy.matcher(input);
        Matcher mFrom = pFrom.matcher(input);
        Matcher mTo = pTo.matcher(input);
        Matcher mUpdate = pUpdate.matcher(input);

        String captured = mUpdate.group(1);
        int taskNumber = Integer.parseInt(captured);
        if (taskNumber < 1 || taskNumber > taskList.getLength()) {
            return "please input a valid number for task to be updated";
        }
        if (mTodo.find()) {
            String newInput = input.substring(mUpdate.end()).replace("todo", "");
            Todo n = new Todo(newInput, false);
            if (newInput.trim().equals("")) {
                return "Task cannot be empty!";
            } else {
                taskList.replace(taskNumber - 1, n);
                storage.edit(taskNumber - 1, n.export());
                String result = "OK, I have successfully updated this task :\n";
                result = result + n + "\n";
                result = result + "You now have " + taskList.getLength() + " items in the list.";
                return result;
            }
        } else if (mEvent.find()) {
            if (mFrom.find() && mTo.find()) {
                String newInput = input.substring(mUpdate.end()).trim();
                int startIndex = newInput.indexOf("/from");
                int startIndexTo = newInput.indexOf("/to");

                String subFrom = newInput.substring(startIndex + 5, startIndexTo).trim();
                String subTo = newInput.substring(startIndexTo + 3).trim();
                String nextInput = newInput.substring(newInput.indexOf("event") + 5, startIndex);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime ldt;
                LocalDateTime ldt2;
                try {
                    ldt = LocalDateTime.parse(subFrom, formatter);
                    ldt2 = LocalDateTime.parse(subTo, formatter);
                } catch (DateTimeParseException e) {
                    // Handle parsing exceptions
                    return "Please enter a valid date/time";
                }
                if (nextInput.trim().equals("")) {
                    return "Task cannot be empty!";
                } else {
                    Event n = new Event(nextInput, false, ldt, ldt2);
                    taskList.replace(taskNumber - 1, n);
                    storage.edit(taskNumber - 1, n.export());
                    String result = "OK, I have successfully updated this task :\n";
                    result = result + n + "\n";
                    result = result + "You now have " + taskList.getLength() + " items in the list.";
                    return result;
                }
            } else {
                return "pls input your start and end of the event.";
            }

        } else if (mDeadline.find()) {
            if (mBy.find()) {
                String newInput = input.substring(mUpdate.end()).trim();
                int finalIndex = newInput.indexOf("/by") + 3;
                String dL = newInput.substring(finalIndex);
                String nextInput = newInput.substring(newInput.indexOf("deadline") + 8, newInput.indexOf("/by"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MM-dd HH:mm");
                LocalDateTime ldt;
                try {
                    // Parse the string into a LocalDate object
                    ldt = LocalDateTime.parse(dL, formatter);
                } catch (DateTimeParseException e) {
                    return "Please enter a valid date/time";
                }
                if (nextInput.trim().equals("")) {
                    return "Task cannot be empty!";
                } else {
                    Deadline n = new Deadline(nextInput, false, ldt);
                    taskList.replace(taskNumber - 1, n);
                    storage.edit(taskNumber - 1, n.export());
                    String result = "OK, I have successfully updated this task :\n";
                    result = result + n + "\n";
                    result = result + "You now have " + taskList.getLength() + " items in the list.";
                    return result;
                }
            } else {
                return "please include a deadline";
            }
        } else {
            return "error! please ensure your replacement task details are correct";
        }
    }
}
