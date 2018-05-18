/*
  TaskTrackerGUI.java

  @author Jon Thomas
 * GUI interface for TaskNode data structure.  Allows maintaining an active
 * task-list for keeping track of due-dates of pending assignments.
 */

package Controller;

/*--------------------*/
/*       IMPORTS
/*--------------------*/

import dataStructure.TaskNode;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskTrackerGUI extends Application {

    /*--------------------*/
    /*  GLOBAL VARIABLES
    /*--------------------*/

    private static final double VERSIONID = 0.01;
    // screen width and height
    private static double WIDTH;
    private static double HEIGHT;
    // data structure
    private TaskNode head;

    // GUI elements
    private Stage stage;
    private BorderPane window;
    private TreeItem<TaskNode> root;
    private Label currSelected;
    private Label dueDate;
    private Label taskAmount;
    private Button markComplete;
    private Button subdivide;
    private Label isComplete;
    private StackPane rootpane;
    private TreeView<TaskNode> taskList;

    /*--------------------*/
    /*      METHODS
    /*--------------------*/

    // main
    public static void main(String[] args) {
        WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("TaskTracker v" + VERSIONID);

        // read in input file
        readFile();

        // init labels/buttons/etc
        initGUIElements();

        // setup window elements
        setupWindow();
    }

    // setupWindow
    // Handle initializing and setting up window elements
    private void setupWindow() {

        // init view panels
        setupPanels();

        // set up stage
        Scene scene = new Scene(window, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream
                        ("taskTree.txt");
                ObjectOutputStream objectOutputStream = new
                        ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(head);
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }

    // initGUIElements
    // init the labels and buttons of the GUI
    private void initGUIElements() {
        currSelected = new Label("Select a task from the list above.");
        dueDate = new Label("");
        taskAmount = new Label("");
        isComplete = new Label("");
        markComplete = new Button("Toggle Completion");
        subdivide = new Button("Sub-Divide");
    }

    // readFile
    // read in taskTree.txt.  If file doesn't exist, create empty tree instead.
    private void readFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("taskTree.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream
                    (fileInputStream);
            head = (TaskNode) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            head = new TaskNode("Task List", LocalDateTime.now());
        }
    }

    // setupPanels
    // setup all gridpane panels
    private void setupPanels() {
        // build tree view structure
        buildTreePane();

        InitializeEventHandlers();

        // build info pane structure
        GridPane infoPane = makeInfoPane();

        // panel to be added to split pane
        GridPane panelView = new GridPane();
        panelView.setVgap(10);
        panelView.add(rootpane, 0, 0);
        panelView.add(infoPane, 0, 1);

        // create main content pane
        GridPane mainContent = new GridPane();
        mainContent.setHgap(20);
        mainContent.setVgap(20);

        // make main gridpane panels
        GridPane taskCreation = buildCreationPane();
        GridPane getNextTask = getNextTaskGridPane();

        // set padding for each panel
        taskCreation.setPadding(new Insets(20, 20, 20, 20));
        getNextTask.setPadding(new Insets(20, 20, 20, 20));

        // add to main content pane
        mainContent.add(taskCreation, 0, 0);
        mainContent.add(getNextTask, 0, 1);

        // create panel view
        makePanelView(panelView, mainContent);
    }

    // makePanelView
    // Create panel views that display tasks on right side of screen
    private void makePanelView(GridPane panelView, GridPane mainContent) {
        ToggleButton tasks = new ToggleButton("Tasks");
        SplitPane splitPane = new SplitPane();
        TitledPane mainTask = new TitledPane("View Tasks", panelView);
        VBox taskPane = new VBox(mainTask);
        taskPane.setMinWidth(0);
        splitPane.getItems().addAll(mainContent, taskPane);

        DoubleProperty splitPaneDividerPosition = splitPane.getDividers().get
                (0).positionProperty();

        splitPaneDividerPosition.addListener((obs, oldPos, newPos) ->
                tasks.setSelected(newPos.doubleValue() < 0.95));

        splitPaneDividerPosition.set(0.8);

        tasks.setOnAction(event -> {
            if (tasks.isSelected()) {
                splitPane.setDividerPositions(0.8);
            } else {
                splitPane.setDividerPositions(1.0);
            }
        });

        window = new BorderPane(splitPane, new HBox(tasks), null, null, null);
    }

    // getNextTaskGridPane
    // create GridPane for getting the next task
    private GridPane getNextTaskGridPane() {
        GridPane getNextTask = new GridPane();
        getNextTask.setVgap(10);
        getNextTask.setHgap(10);
        Label categoryString = new Label("Click Button to Select Next Task");
        Button getTask = new Button("Get Next Task");
        getTask.setOnAction(actionEvent -> {
            TaskNode taskNode = head.chooseTask();
            categoryString.setText("Next Task: " + taskNode.getCategory());
        });
        getNextTask.add(categoryString, 0, 0);
        getNextTask.add(getTask, 0, 1);
        return getNextTask;
    }

    // makeInfoPane
    // create infoPane GridPane
    private GridPane makeInfoPane() {
        GridPane infoPane = new GridPane();
        infoPane.setVgap(10);
        infoPane.add(currSelected, 0, 0);
        infoPane.add(taskAmount, 0, 2);
        infoPane.add(dueDate, 0, 1);
        infoPane.add(isComplete, 0, 3);
        infoPane.add(markComplete, 0, 4);
        infoPane.add(subdivide, 0, 5);
        return infoPane;
    }

    // buildCreationPane
    // build GridPane for task creation pane
    private GridPane buildCreationPane() {
        GridPane taskCreation = new GridPane();
        Label createTask = new Label("Create Task");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Task Name");

        TextField taskCount = new TextField();
        taskCount.setPromptText("# of tasks");

        TextField startIndex = new TextField();
        startIndex.setPromptText("Starting Index");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Due Date");
        TextField hour = new TextField();
        hour.setPromptText("Hour");
        TextField minute = new TextField();
        minute.setPromptText("Minute");
        RadioButton pm = new RadioButton("PM");

        Button addTask = new Button("Add Task");
        addTask.setOnAction(actionEvent -> {
            try {
                String category = categoryField.getText();
                int h = Integer.parseInt(hour.getText());
                int m = Integer.parseInt(minute.getText());

                if (h < 0 || h > 23) {
                    throw new IllegalArgumentException();
                }

                if (m < 0 || m > 59) {
                    throw new IllegalArgumentException();
                }

                if (h == 12) {
                    h = 0;
                }

                if (pm.isSelected()) {
                    h += 12;
                }

                int tasks = 0;
                int startingIndex = 0;

                if (taskCount.getText() != null) {
                    tasks = Integer.parseInt(taskCount.getText());
                }

                if (startIndex.getText() != null) {
                    startingIndex = Integer.parseInt(startIndex.getText());
                }

                LocalTime time = LocalTime.of(h, m);
                LocalDateTime dueDate = LocalDateTime.of(datePicker.getValue
                        (), time);
                TaskNode taskNode = new TaskNode(category, dueDate);
                taskNode.setTaskCount(tasks + startingIndex);
                taskNode.setStartingIndex(startingIndex);

                TaskNode parent = head;

                if (!taskList.getSelectionModel().isEmpty()) {
                    parent = taskList.getSelectionModel()
                            .getSelectedItem().getValue();
                }

                parent.addChild(taskNode);
                rebuildTreeView();
            } catch (Exception ignored) {
            }
        });

        taskCreation.setVgap(10);
        taskCreation.setHgap(10);
        taskCreation.add(createTask, 0, 0);
        taskCreation.add(categoryField, 0, 1);
        taskCreation.add(taskCount, 1, 1);
        taskCreation.add(startIndex, 2, 1);
        taskCreation.add(datePicker, 0, 2);
        taskCreation.add(hour, 1, 2);
        taskCreation.add(minute, 2, 2);
        taskCreation.add(pm, 3, 2);
        taskCreation.add(addTask, 0, 3);
        return taskCreation;
    }

    // initializeEventHandlers
    // create event handlers for task selection pane
    private void InitializeEventHandlers() {
        markComplete.setOnAction(actionEvent -> {
            if (!taskList.getSelectionModel().isEmpty()) {
                TaskNode node = taskList.getSelectionModel()
                        .getSelectedItem().getValue();
                node.setCompleted(!node.isCompleted());
                rebuildTreeView();
            }
        });

        subdivide.setOnAction(actionEvent -> {
            if (!taskList.getSelectionModel().isEmpty()) {
                TaskNode node = taskList.getSelectionModel()
                        .getSelectedItem().getValue();
                node.subDivide(LocalDateTime.now());
                rebuildTreeView();
            }
        });

        taskList.setOnMousePressed(mouseEvent -> {
            if (!taskList.getSelectionModel().isEmpty()) {
                TaskNode node = taskList.getSelectionModel()
                        .getSelectedItem().getValue();
                currSelected.setText(node.toString());
                dueDate.setText(node.getDueDateString());
                taskAmount.setText("Sub-Task Count: " + node.getTaskCount());
                String s = "NO";
                if (node.isCompleted()) {
                    s = "YES";
                }
                isComplete.setText("Completed: " + s);
            }
        });
    }

    // rebuildTreeView
    // Restructure tree view to show updated data structure
    private void rebuildTreeView() {
        root = buildSubTree(head);
        taskList.setRoot(root);
        root.setExpanded(true);
    }

    // buildTreePane
    // build tree view to display data structure
    private void buildTreePane() {
        root = buildSubTree(head);
        root.setExpanded(true);
        taskList = new TreeView<>(root);
        rootpane = new StackPane();
        rootpane.getChildren().add(taskList);
        taskList.setPrefWidth(WIDTH);
    }

    // buildSubTree
    // recursively parse tree to build tree view representing data structure
    private TreeItem<TaskNode> buildSubTree(TaskNode node) {
        TreeItem<TaskNode> item = new TreeItem<>(node);
        int index = 0;
        TaskNode walker = node.getChild(index);

        while (walker != null) {
            item.getChildren().add(buildSubTree(walker));
            item.setExpanded(true);
            index++;
            walker = node.getChild(index);
        }
        return item;
    }
}