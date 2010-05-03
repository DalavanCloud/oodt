//Copyright (c) 2006, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//$Id$

package gov.nasa.jpl.oodt.cas.resource.system;

//OODT imports
import gov.nasa.jpl.oodt.cas.resource.scheduler.Scheduler;
import gov.nasa.jpl.oodt.cas.resource.structs.Job;
import gov.nasa.jpl.oodt.cas.resource.structs.JobInput;
import gov.nasa.jpl.oodt.cas.resource.structs.JobSpec;
import gov.nasa.jpl.oodt.cas.resource.structs.ResourceNode;
import gov.nasa.jpl.oodt.cas.resource.structs.exceptions.JobExecutionException;
import gov.nasa.jpl.oodt.cas.resource.structs.exceptions.JobQueueException;
import gov.nasa.jpl.oodt.cas.resource.structs.exceptions.JobRepositoryException;
import gov.nasa.jpl.oodt.cas.resource.structs.exceptions.MonitorException;
import gov.nasa.jpl.oodt.cas.resource.structs.exceptions.SchedulerException;
import gov.nasa.jpl.oodt.cas.resource.util.GenericResourceManagerObjectFactory;
import gov.nasa.jpl.oodt.cas.resource.util.XmlRpcStructFactory;

//APACHE imports
import org.apache.xmlrpc.WebServer;

//JDK imports
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author woollard
 * @version $Revision$
 * 
 * <p>
 * An XML RPC-based Resource manager.
 * </p>
 * 
 */
public class XmlRpcResourceManager {

    /* the port to run the XML RPC web server on, default is 2000 */
    private int webServerPort = 2000;

    /* our log stream */
    private Logger LOG = Logger
            .getLogger(XmlRpcResourceManager.class.getName());

    /* our xml rpc web server */
    private WebServer webServer = null;

    /* our scheduler */
    private Scheduler scheduler = null;

    public XmlRpcResourceManager(int port) throws Exception {
        // load properties from workflow manager properties file, if specified
        if (System.getProperty("gov.nasa.jpl.oodt.cas.resource.properties") != null) {
            String configFile = System
                    .getProperty("gov.nasa.jpl.oodt.cas.resource.properties");
            LOG.log(Level.INFO,
                    "Loading Resource Manager Configuration Properties from: ["
                            + configFile + "]");
            System.getProperties().load(
                    new FileInputStream(new File(configFile)));
        }

        String schedulerClassStr = System.getProperty(
                "resource.scheduler.factory",
                "gov.nasa.jpl.oodt.cas.resource.scheduler.LRUSchedulerFactory");

        scheduler = GenericResourceManagerObjectFactory
                .getSchedulerServiceFromFactory(schedulerClassStr);

        // start up the scheduler
        new Thread(scheduler).start();

        webServerPort = port;

        // start up the web server
        webServer = new WebServer(webServerPort);
        webServer.addHandler("resourcemgr", this);
        webServer.start();

        LOG.log(Level.INFO, "Resource Manager started by "
                + System.getProperty("user.name", "unknown"));

    }

    public boolean isAlive() {
        return true;
    }
    
    /**
     * Gets the number of Jobs in JobQueue
     * @return Number of Jobs in JobQueue
     * @throws JobRepositoryException On Any Exception
     */
    public int getJobQueueSize() throws JobRepositoryException {
    	try {
    		return this.scheduler.getJobQueue().getSize();
    	}catch (Exception e) {
    		throw new JobRepositoryException("Failed to get size of JobQueue : " + e.getMessage(), e);
    	}
    }
    
    /**
     * Gets the max number of Jobs allowed in JobQueue
     * @return Max number of Jobs
     * @throws JobRepositoryException On Any Exception
     */
    public int getJobQueueCapacity() throws JobRepositoryException {
    	try {
    		return this.scheduler.getJobQueue().getCapacity();
    	}catch (Exception e) {
    		throw new JobRepositoryException("Failed to get capacity of JobQueue : " + e.getMessage(), e);
    	}
    }

    public boolean isJobComplete(String jobId) throws JobRepositoryException {
        JobSpec spec = scheduler.getJobQueue().getJobRepository().getJobById(
                jobId);
        return scheduler.getJobQueue().getJobRepository().jobFinished(spec);
    }

    public Hashtable getJobInfo(String jobId) throws JobRepositoryException {
        JobSpec spec = null;

        try {
            spec = scheduler.getJobQueue().getJobRepository()
                    .getJobById(jobId);
        } catch (JobRepositoryException e) {
            LOG.log(Level.WARNING,
                    "Exception communicating with job repository for job: ["
                            + jobId + "]: Message: " + e.getMessage());
            throw new JobRepositoryException("Unable to get job: [" + jobId
                    + "] from repository!");
        }

        return XmlRpcStructFactory.getXmlRpcJob(spec.getJob());
    }

    public String handleJob(Hashtable jobHash, Hashtable jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, jobIn);
    }

    public String handleJob(Hashtable jobHash, int jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, new Integer(jobIn));
    }

    public String handleJob(Hashtable jobHash, boolean jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, new Boolean(jobIn));
    }

    public String handleJob(Hashtable jobHash, String jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, jobIn);
    }

    public String handleJob(Hashtable jobHash, double jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, new Double(jobIn));
    }

    public String handleJob(Hashtable jobHash, Date jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, jobIn);
    }

    public String handleJob(Hashtable jobHash, Vector jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, jobIn);
    }

    public String handleJob(Hashtable jobHash, byte[] jobIn)
            throws SchedulerException {
        return genericHandleJob(jobHash, jobIn);
    }

    public boolean handleJob(Hashtable jobHash, Hashtable jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, jobIn, urlStr);
    }

    public boolean handleJob(Hashtable jobHash, int jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, new Integer(jobIn), urlStr);
    }

    public boolean handleJob(Hashtable jobHash, boolean jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, new Boolean(jobIn), urlStr);
    }

    public boolean handleJob(Hashtable jobHash, String jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, jobIn, urlStr);
    }

    public boolean handleJob(Hashtable jobHash, double jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, new Double(jobIn), urlStr);
    }

    public boolean handleJob(Hashtable jobHash, Date jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, jobIn, urlStr);
    }

    public boolean handleJob(Hashtable jobHash, Vector jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, jobIn, urlStr);
    }

    public boolean handleJob(Hashtable jobHash, byte[] jobIn, String urlStr)
            throws JobExecutionException {
        return genericHandleJob(jobHash, jobIn, urlStr);
    }

    public List getNodes() throws MonitorException {
        List resNodes = scheduler.getMonitor().getNodes();
        return XmlRpcStructFactory.getXmlRpcResourceNodeList(resNodes);
    }

    public Hashtable getNodeById(String nodeId) throws MonitorException {
        ResourceNode node = scheduler.getMonitor().getNodeById(nodeId);
        return XmlRpcStructFactory.getXmlRpcResourceNode(node);

    }

    public boolean killJob(String jobId) throws MonitorException {
        String resNodeId = scheduler.getBatchmgr().getExecutionNode(jobId);
        if (resNodeId == null) {
            LOG.log(Level.WARNING, "Attempt to kill job: [" + jobId
                    + "]: cannot find execution node"
                    + " (has the job already finished?)");
            return false;
        }
        ResourceNode node = scheduler.getMonitor().getNodeById(resNodeId);
        return scheduler.getBatchmgr().killJob(jobId, node);
    }

    public String getExecutionNode(String jobId) {
        String execNode = scheduler.getBatchmgr().getExecutionNode(jobId);
        if (execNode == null) {
            LOG.log(Level.WARNING, "Job: [" + jobId
                    + "] not currently executing on any known node");
            return "";
        } else
            return execNode;
    }

    public static void main(String[] args) throws Exception {
        int portNum = -1;
        String usage = "XmlRpcResourceManager --portNum <port number for xml rpc service>\n";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--portNum")) {
                portNum = Integer.parseInt(args[++i]);
            }
        }

        if (portNum == -1) {
            System.err.println(usage);
            System.exit(1);
        }

        XmlRpcResourceManager manager = new XmlRpcResourceManager(portNum);

        for (;;)
            try {
                Thread.currentThread().join();
            } catch (InterruptedException ignore) {
            }
    }

    private String genericHandleJob(Hashtable jobHash, Object jobIn)
            throws SchedulerException {

        Job exec = XmlRpcStructFactory.getJobFromXmlRpc(jobHash);
        JobInput in = GenericResourceManagerObjectFactory
                .getJobInputFromClassName(exec.getJobInputClassName());
        in.read(jobIn);

        JobSpec spec = new JobSpec(in, exec);

        // queue the job up
        String jobId = null;

        try {
            jobId = scheduler.getJobQueue().addJob(spec);
        } catch (JobQueueException e) {
            LOG.log(Level.WARNING, "JobQueue exception adding job: Message: "
                    + e.getMessage());
            throw new SchedulerException(e.getMessage());
        }
        return jobId;
    }

    private boolean genericHandleJob(Hashtable jobHash, Object jobIn,
            String urlStr) throws JobExecutionException {
        Job exec = XmlRpcStructFactory.getJobFromXmlRpc(jobHash);
        JobInput in = GenericResourceManagerObjectFactory
                .getJobInputFromClassName(exec.getJobInputClassName());
        in.read(jobIn);

        JobSpec spec = new JobSpec(in, exec);

        URL remoteUrl = safeGetUrlFromString(urlStr);
        ResourceNode remoteNode = null;

        try {
            remoteNode = scheduler.getMonitor().getNodeByURL(remoteUrl);
        } catch (MonitorException e) {
        }

        if (remoteNode != null) {
            return scheduler.getBatchmgr().executeRemotely(spec, remoteNode);
        } else
            return false;
    }

    private URL safeGetUrlFromString(String urlStr) {
        URL url = null;

        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            LOG.log(Level.WARNING, "Error converting string: [" + urlStr
                    + "] to URL object: Message: " + e.getMessage());
        }

        return url;
    }
}