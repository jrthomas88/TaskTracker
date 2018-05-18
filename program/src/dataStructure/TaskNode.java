package dataStructure;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class TaskNode implements Serializable, Comparable<TaskNode> {

    private boolean isCompleted;
    private boolean isParent;
    private int numOfChildren;
    private int taskCount;
    private int startingIndex;
    private int endingIndex;

    private String category;

    // the due date of this task
    private int year;
    private int month;
    private int day;
    private int hour; // 0 - 23, military time
    private int minute;

    // the due date of my earliest child
    private int yearChild;
    private int monthChild;
    private int dayChild;
    private int hourChild; // 0 - 23, military time
    private int minuteChild;

    // list of children
    private List<TaskNode> children;

    // parent
    private TaskNode parent;

    public TaskNode(String category, int year, int month, int day, int hour,
                    int minute) {
        init(category, year, month, day, hour, minute);
    }

    public TaskNode(String category, LocalDateTime dueDate) {
        init(category, dueDate.getYear(), dueDate.getMonthValue(),
                dueDate.getDayOfMonth(), dueDate.getHour(), dueDate.getMinute());
    }

    private void init(String category, int year, int month, int day, int hour, int minute) {
        this.category = category;
        this.year = yearChild = year;
        this.month = monthChild = month;
        this.day = dayChild = day;
        this.hour = hourChild = hour;
        this.minute = minuteChild = minute;

        numOfChildren = 0;
        isCompleted = false;
        isParent = false;

        children = new LinkedList<>();
        parent = null;
    }

    /* Getters and Setters */

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;

        for (TaskNode t : children) {
            t.setCompleted(true);
        }

        if (completed && parent != null) {
            parent.removeChildren();
        }
    }

    private int getNumOfChildren() {
        return numOfChildren;
    }

    public String getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    private int getYearChild() {
        return yearChild;
    }

    private int getMonthChild() {
        return monthChild;
    }

    private int getDayChild() {
        return dayChild;
    }

    private int getHourChild() {
        return hourChild;
    }

    private int getMinuteChild() {
        return minuteChild;
    }

    private void setParent(TaskNode parent) {
        this.parent = parent;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void setStartingIndex(int startingIndex) {
        this.startingIndex = startingIndex;
    }

    private int getEndingIndex() {
        return endingIndex;
    }

    private void setEndingIndex(int endingIndex) {
        this.endingIndex = endingIndex;
    }

    /* End Getters and Setters */

    /**
     * compareTo
     * Return which node has a task that is due first.
     *
     * @param taskNode the other node comparing to
     * @return an integer >0 if this node is due after the other node, <0 if
     * this node is due before the other node, 0 if the two nodes are due at the
     * same time.
     */

    @Override
    public int compareTo(TaskNode taskNode) {
        int otherYearChild = taskNode.getYearChild();
        int otherMonthChild = taskNode.getMonthChild();
        int otherDayChild = taskNode.getDayChild();
        int otherHourChild = taskNode.getHourChild();
        int otherMinuteChild = taskNode.getMinuteChild();

        int otherYear = taskNode.getYear();
        int otherMonth = taskNode.getMonth();
        int otherDay = taskNode.getDay();
        int otherHour = taskNode.getHour();
        int otherMinute = taskNode.getMinute();

        if (otherYearChild != yearChild) {
            return yearChild - otherYearChild;
        }

        if (otherMonthChild != monthChild) {
            return monthChild - otherMonthChild;
        }

        if (otherDayChild != dayChild) {
            return dayChild - otherDayChild;
        }

        if (otherHourChild != hourChild) {
            return hourChild - otherHourChild;
        }

        if (otherMinuteChild != minuteChild) {
            return minuteChild - otherMinuteChild;
        }

        if (otherYear != year) {
            return year - otherYear;
        }

        if (otherMonth != month) {
            return month - otherMonth;
        }

        if (otherDay != day) {
            return day - otherDay;
        }

        if (otherHour != hour) {
            return hour - otherHour;
        }

        if (otherMinute != minute) {
            return minute - otherMinute;
        }

        return 0;
    }

    public void addChild(TaskNode child) {
        children.add(child);
        numOfChildren++;
        isParent = true;
        isCompleted = false;
        child.setParent(this);

        boolean resetChild = false;

        if (child.getYear() < yearChild) {
            resetChild = true;
        } else if (child.getYear() > yearChild) {
            resetChild = false;
        } else {
            if (child.getMonth() < monthChild) {
                resetChild = true;
            } else if (child.getMonth() > monthChild) {
                resetChild = false;
            } else {
                if (child.getDay() < dayChild) {
                    resetChild = true;
                } else if (child.getDay() > dayChild) {
                    resetChild = false;
                } else {
                    if (child.getHour() < hourChild) {
                        resetChild = true;
                    } else if (child.getHour() > hourChild) {
                        resetChild = false;
                    } else {
                        if (child.getMinute() < minuteChild) {
                            resetChild = true;
                        } else if (child.getMinute() >= minuteChild) {
                            resetChild = false;
                        }
                    }
                }
            }
        }

        if (resetChild) {
            yearChild = child.yearChild;
            monthChild = child.getMonthChild();
            dayChild = child.getDayChild();
            hourChild = child.getHourChild();
            minuteChild = child.getMinuteChild();
        }

    }

    private void removeChildren() {

        boolean reset = true;

        for (TaskNode t : children) {
            if (!t.isCompleted()) {
                reset = false;
            }
        }

        if (reset) {
            children = new LinkedList<>();
            setCompleted(true);
        }
    }

    public TaskNode chooseTask() {
        // if I have no children, return me
        if (!isParent) {
            return this;
        } else {
            LinkedList<TaskNode> childTasks = new LinkedList<>();
            for (TaskNode t : children) {
                if (!t.isCompleted()) {
                    childTasks.add(t.chooseTask());
                }
            }

            //assert !childTasks.isEmpty();

            TaskNode min = childTasks.get(0);

            for (int i = 1; i < childTasks.size(); i++) {
                int compare = childTasks.get(i).compareTo(min);
                if (compare < 0) {
                    min = childTasks.get(i);
                } else if (compare == 0) {
                    if (childTasks.get(i).getNumOfChildren() > min
                            .getNumOfChildren()) {
                        min = childTasks.get(i);
                    }
                }
            }
            return min;
        }
    }

    public LocalDateTime subDivide(LocalDateTime startingPoint) {

        System.out.println("Task Count: " + taskCount);
        System.out.println("Start: " + startingIndex);
        System.out.println("Ending Index: " + endingIndex);

        // update starting index based on completed children
        if (taskCount > 0 && children.size() > 0) {
            for (TaskNode t : children) {
                if (t.isCompleted()) {
                    startingIndex = t.getEndingIndex() + 1;
                } else {
                    break;
                }
            }
            children = new LinkedList<>();
            isParent = false;
        }

        // if a parent, sub-divide my children
        if (isParent) {
            for (TaskNode t : children) {
                if (!t.isCompleted()) {
                    startingPoint = t.subDivide(startingPoint);
                }
            }
            return startingPoint;
        }

        // get number of days until task is due
        LocalDateTime then = LocalDateTime.of(year, month, day, hour, minute);
        int numOfDays = (int) startingPoint.until(then, DAYS);

        // if there are no tasks, or no days until due, then cannot sub-divide
        if (taskCount == 0 || numOfDays <= 0) {
            return then;
        }

        // get daily rate
        double tasksPerDay = ((double) taskCount) / numOfDays;
        int dayCount = 0;

        LocalDateTime newTime = then;

        for (double i = startingIndex; i < taskCount; i += tasksPerDay) {

            // figure out ending task of the day
            double end = i + tasksPerDay;
            if ((int) end - (int) i == 0) { // if tomorrow will be the same task
                dayCount++; // skip today
                continue;
            } else {
                end = end - 1; // subtract 1 from the ending index to not repeat
            }
            if (end >= taskCount) {
                end = taskCount - 1;
            }

            String newcategory;

            if ((int) end - (int) i == 0) { // if end and start are same index
                newcategory = category + ": " + (int) i;
            } else {
                newcategory = category + ": " + (int) i + " - " + (int) end;
            }

            newTime = startingPoint.plusDays(dayCount);
            TaskNode taskNode = new TaskNode(newcategory, newTime);
            taskNode.setStartingIndex((int) i);
            taskNode.setEndingIndex((int) end);
            taskNode.setParent(this);
            children.add(taskNode);
            isParent = true;
            dayCount++;

        }
        return newTime;
    }

    public void printTree(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print('\t');
        }
        System.out.println(category + ": due " + year + " " + month + " " + day
                           + " - " + hour + ":" + minute);
        for (TaskNode t : children) {
            t.printTree(indent + 1);
        }
    }

    public TaskNode getChild(int index) {
        if (index > children.size() - 1) {
            return null;
        }
        return children.get(index);
    }

    public String toString() {
        return category;
    }

    public String getDueDateString() {
        LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute);
        String dueDate = year + " " + time.getMonth() + " " + day + ", ";
        boolean isAM = true;
        if (hour >= 12) {
            hour -= 12;
            isAM = false;
        }
        if (hour == 0) {
            hour = 12;
        }
        String min = String.format("%02d", minute);
        dueDate += hour + ":" + min + " ";
        if (isAM) {
            dueDate += "am";
        } else {
            dueDate += "pm";
        }
        return dueDate;
    }
}
