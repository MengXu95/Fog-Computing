/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package mengxu.taskscheduling;

//import org.cloudbus.cloudsim.Log;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WorkflowParser parse a DAX into tasks so that WorkflowSim can manage them
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Aug 23, 2013
 * @date Nov 9, 2014
 */
public class WorkflowParser {

    /**
     * All tasks.
     */
    private List<Task> taskList;

    /**
     * current job id. In case multiple workflow submission
     */
    private int jobIdStartsFrom;


    /**
     * Gets the task list
     *
     * @return the task list
     */
    @SuppressWarnings("unchecked")
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * Sets the task list
     *
     * @param taskList the task list
     */
    protected void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
    /**
     * Map from task name to task.
     */
    protected Map<String, Task> mName2Task;
//    protected Map<String, String> mName2Task;

    /**
     * Initialize a WorkflowParser
     *
     * @param daxPath the path of DAX. Currently we have just checked single user
     * mode
     */
    public WorkflowParser(String daxPath) {
        this.mName2Task = new HashMap<>();
        this.jobIdStartsFrom = 0;
        setTaskList(new ArrayList<>());
        GenerateWorkflowJobFromXmlFile(daxPath);
    }

    /**
     * Start to parse a workflow which is a xml file(s).
     * Parse a DAX file with jdom
     */
    private void GenerateWorkflowJobFromXmlFile(String path) {
        try {

            SAXBuilder builder = new SAXBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = builder.build(new File(path));
            Element root = dom.getRootElement();
            List<Element> list = root.getChildren();
            for (Element node : list) {
                switch (node.getName().toLowerCase()) {
                    case "job":
                        long length = 0;
                        String nodeName = node.getAttributeValue("id");
                        String nodeType = node.getAttributeValue("name");

                        Task task;
                        //In case of multiple workflow submission. Make sure the jobIdStartsFrom is consistent.
                        synchronized (this) {
                            task = new Task(this.jobIdStartsFrom);
                            this.jobIdStartsFrom++;
                        }
                        mName2Task.put(nodeName, task);
                        this.getTaskList().add(task);

                        /**
                         * Add dependencies info.
                         */
                        break;
                    case "child":
                        List<Element> pList = node.getChildren();
                        String childName = node.getAttributeValue("ref");
                        if (mName2Task.containsKey(childName)) {

                            Task childTask = (Task) mName2Task.get(childName);

                            for (Element parent : pList) {
                                String parentName = parent.getAttributeValue("ref");
                                if (mName2Task.containsKey(parentName)) {
                                    Task parentTask = (Task) mName2Task.get(parentName);
                                    parentTask.addChild(childTask);
                                    childTask.addParent(parentTask);
                                }
                            }
                        }
                        break;
                }
            }
            /**
             * Clean them so as to save memory. Parsing workflow may take much
             * memory
             */
            this.mName2Task.clear();

        } catch (JDOMException jde) {
            System.out.println("JDOM Exception;Please make sure your dax file is valid");

        } catch (IOException ioe) {
            System.out.println("IO Exception;Please make sure dax.path is correctly set in your config file");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Parsing Exception");
        }
    }
}
