package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class IgniteNode {
    public static void main(String[] args) throws InterruptedException {
        int nodeId = Integer.parseInt(args[0]);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("ignite-node" + nodeId);
        cfg.setConsistentId("node-" + nodeId);
        cfg.setLocalHost("127.0.0.1");

        //discovery config
        //seed address every node that starts will to contact to join the cluster
        // seed node is 47502
        //can use Multicast/Kubernetes ipfinder for prod
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(
                Collections.singletonList("127.0.0.1:47502")
        );
        //discovery
        //discovers other ignite nodes
        //for membership and topology not for data transfer
        TcpDiscoverySpi discovery = new TcpDiscoverySpi();
        discovery.setLocalPort(47500 + nodeId);
        discovery.setLocalPortRange(0);
        discovery.setIpFinder(ipFinder);

        //communication nodes chatting with data
        TcpCommunicationSpi communication = new TcpCommunicationSpi();
        communication.setLocalPort(41700 + nodeId);
        communication.setLocalPortRange(0);

        //set config's communication
        cfg.setCommunicationSpi(communication);

        //thin client connector
        //allows external clients to connect
        ClientConnectorConfiguration connector = new ClientConnectorConfiguration();
        connector.setPort(10800 + nodeId);
        connector.setPortRange(0);

        cfg.setClientConnectorConfiguration(connector);

        //separate working directory
        String directoryPath = System.getProperty("user.dir");
        String workDir = Paths.get(directoryPath, "work", "node-" + nodeId).toAbsolutePath().toString();
        cfg.setWorkDirectory(workDir);
        // cfg.setWorkDirectory("work/node-" + nodeId);

        //start here
        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.println("ignite node " + nodeId + " started...");
            System.out.println("Cluster nodes: " + ignite.cluster().nodes().size());
        }
        //keep it running
        new CountDownLatch(1).await();
    }

}