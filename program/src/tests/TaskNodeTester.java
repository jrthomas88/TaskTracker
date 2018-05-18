package tests;

import dataStructure.TaskNode;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskNodeTester {

    @Test
    public void testConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TaskNode node = new TaskNode("sample", now.getYear(), now
                .getMonthValue(), now.getDayOfMonth(), now.getHour(), now
                .getMinute());
        assertTrue(node.getCategory().equals("sample"));
        assertEquals(now.getYear(), node.getYear());
        assertEquals(now.getMonthValue(), node.getMonth());
        assertEquals(now.getDayOfMonth(), node.getDay());
        assertEquals(now.getHour(), node.getHour());
        assertEquals(now.getMinute(), node.getMinute());
        node.printTree(0);
    }

    @Test
    public void testSubDivide() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(30);
        TaskNode node = new TaskNode("sample", dueDate.getYear(), dueDate
                .getMonthValue(), dueDate.getDayOfMonth(), dueDate.getHour(),
                dueDate.getMinute());
        node.setTaskCount(100);
        node.subDivide(LocalDateTime.now());
        //node.printTree(0);
    }

    @Test
    public void testComplexTree() {
        String projectName = "Final CSC Project";
        String partner = "Find project partner";
        String outline = "Develop project outline";
        String testcases = "Construct a set of testcases";
        String passTests = "Pass all testcases";
        String comments = "Comment and document code";

        LocalDateTime due = LocalDateTime.now().plusDays(30);
        TaskNode node = new TaskNode(projectName, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        due = due.minusDays(27);
        TaskNode p = new TaskNode(partner, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        due = due.plusDays(2);
        TaskNode o = new TaskNode(outline, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        due = due.plusDays(5);
        TaskNode t = new TaskNode(testcases, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        t.setTaskCount(10);
        due = due.plusDays(12);
        TaskNode pt = new TaskNode(passTests, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        pt.setTaskCount(10);
        due = due.plusDays(8);
        TaskNode c = new TaskNode(comments, due.getYear(), due
                .getMonthValue(), due.getDayOfMonth(), due.getHour(), due
                .getMinute());
        node.addChild(p);
        node.addChild(o);
        node.addChild(t);
        node.addChild(pt);
        node.addChild(c);
        node.subDivide(LocalDateTime.now());
        //node.printTree(0);
        int count = 0;
        while (!node.isCompleted()) {
            count++;
            TaskNode t2 = node.chooseTask();
            //System.out.println(t2.getCategory());
            t2.setCompleted(true);
        }
        assertEquals(18, count);
    }

    @Test
    public void testCorrectSelection() {
        TaskNode t0 = new TaskNode("test01", LocalDateTime.now().plusDays(5));
        TaskNode t1 = new TaskNode("test02", LocalDateTime.now().plusDays(10));
        TaskNode t2 = new TaskNode("test03", LocalDateTime.now().plusDays(2));
        TaskNode head = new TaskNode("head", LocalDateTime.now());

        head.addChild(t0);
        head.addChild(t1);
        head.addChild(t2);

        TaskNode t = head.chooseTask();
        assertEquals(t.getCategory(), "test03");
    }

}
