package org.example;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

public class MyIgniteClient {
    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setAddresses(
                "127.0.0.1:10800",
                "127.0.0.1:10801",
                "127.0.0.1:10802"
        );
        try (IgniteClient client = Ignition.startClient(configuration)) {
            ClientCache<Integer, Object> customers = client.getOrCreateCache("customers");
            customers.put(1, "aelaf");
            customers.put(2, "dave");
            customers.put(3, "negash");
            System.out.println("Customer 1 " + customers.get(1));
            System.out.println("Customer 2 " + customers.get(2));
            System.out.println("Customer 3 " + customers.get(3));
        }
    }
}
