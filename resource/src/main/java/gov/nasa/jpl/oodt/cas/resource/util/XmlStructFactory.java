//Copyright (c) 2005, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//$Id$

package gov.nasa.jpl.oodt.cas.resource.util;

//JDK imports
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

//OODT imports
import gov.nasa.jpl.oodt.cas.commons.xml.XMLUtils;
import gov.nasa.jpl.oodt.cas.resource.structs.Job;
import gov.nasa.jpl.oodt.cas.resource.structs.JobInput;
import gov.nasa.jpl.oodt.cas.resource.structs.JobSpec;
import gov.nasa.jpl.oodt.cas.resource.structs.ResourceNode;

/**
 * @author woollard
 * @version $Revsion$
 * 
 * <p>
 * A class for constructing Resource Manager objects from XML {@link Node}s and
 * {@link Element}s.
 * </p>
 */
public final class XmlStructFactory {

    /* our log stream */
    public static Logger LOG = Logger.getLogger(XmlStructFactory.class
            .getName());

    private XmlStructFactory() throws InstantiationException {
        throw new InstantiationException(
                "Don't instantiate XML Struct Factories!");
    }

    public static ResourceNode getNodes(Node node) {
        Element resourceNodeRoot = (Element) node;

        String id = null;
        URL ip = null;
        int capacity = 0;

        try {
            id = resourceNodeRoot.getAttribute("nodeId");
            ip = new URL(resourceNodeRoot.getAttribute("ip"));
            capacity = new Integer(resourceNodeRoot.getAttribute("capacity"))
                    .intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResourceNode resource = new ResourceNode(id, ip, capacity);

        return resource;
    }

    public static List getQueueAssignment(Node node) {
        Vector queues = new Vector();

        Element resourceNodeRoot = (Element) node;
        Element queueElem = XMLUtils
                .getFirstElement("queues", resourceNodeRoot);
        NodeList queueList = queueElem.getElementsByTagName("queue");

        if (queueList != null && queueList.getLength() > 0) {
            for (int i = 0; i < queueList.getLength(); i++) {
                Element queueElement = (Element) queueList.item(i);

                String queueId = queueElement.getAttribute("name");
                queues.add(queueId);
            }
        }

        return queues;
    }

    public static JobSpec getJobSpec(Node node) {
        Element jobNodeElem = (Element) node;

        String jobId = jobNodeElem.getAttribute("id");
        String jobName = jobNodeElem.getAttribute("name");
        Element instClassElem = XMLUtils.getFirstElement("instanceClass",
                jobNodeElem);
        String instClass = instClassElem.getAttribute("name");
        String queue = XMLUtils.getElementText("queue", jobNodeElem);
        Integer load = new Integer(Integer.parseInt(XMLUtils.getElementText(
                "load", jobNodeElem)));

        Element inputClass = XMLUtils
                .getFirstElement("inputClass", jobNodeElem);
        String inputClassName = inputClass.getAttribute("name");

        // now read the properties defined, if any
        Element propertiesOuterRoot = XMLUtils.getFirstElement("properties",
                inputClass);
        Properties inputConfigProps = null;

        if (propertiesOuterRoot != null) {
            inputConfigProps = new Properties();
            NodeList propNodeList = propertiesOuterRoot
                    .getElementsByTagName("property");

            if (propNodeList != null && propNodeList.getLength() > 0) {
                for (int i = 0; i < propNodeList.getLength(); i++) {
                    Element propElem = (Element) propNodeList.item(i);
                    String propName = propElem.getAttribute("name");
                    String propValue = propElem.getAttribute("value");

                    if (propName != null && propValue != null) {
                        inputConfigProps.setProperty(propName, propValue);
                    }
                }
            }
        }

        Job job = new Job();
        job.setId(jobId);
        job.setName(jobName);
        job.setJobInstanceClassName(instClass);
        job.setJobInputClassName(inputClassName);
        job.setQueueName(queue);
        job.setLoadValue(load);

        JobInput in = GenericResourceManagerObjectFactory
                .getJobInputFromClassName(inputClassName);
        if (inputConfigProps != null) {
            in.configure(inputConfigProps);
        }

        JobSpec spec = new JobSpec(in, job);

        return spec;
    }

}