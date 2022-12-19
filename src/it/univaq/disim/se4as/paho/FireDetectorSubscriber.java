package it.univaq.disim.se4as.paho;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class FireDetectorSubscriber implements MqttCallback {
    MqttClient client;

    @Override
    public void connectionLost(Throwable arg0) {
        System.out.println("The connection with the server is lost. !!!!");

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.out.println("The delivery has been complete. The delivery token is " + arg0.toString());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String messageText = new String(message.getPayload());
        
        if (topic.equals("FireSensor")) {

            System.out.println(String.format(" received Fire: %s", messageText));
            Properties props = new Properties();
            props.load(new StringReader(messageText.substring(1, messageText.length() - 1).replace(", ", "\n")));      
            Map<String, String> map2 = new HashMap<String, String>();
            for (Map.Entry<Object, Object> e : props.entrySet()) {
            map2.put((String)e.getKey(), (String)e.getValue());
            }
            System.out.println(map2.get("FireAlarm"));
            System.out.println(map2.get("Temperatue"));
        }

        // System.out.println("A new message arrived from the topic: \"" + arg0 + "\".
        // The payload of the message is " + message.toString());

    }

    public static void main(String[] args) {

        // new PahoDemoImplCallBack().publish1();
        new FireDetectorSubscriber().subscribe();
    }

    public void subscribe() {
        try {
            client = new MqttClient("tcp://localhost:1883", "FireSensor");

            client.setCallback(this);
            client.connect();
            try {

                client.subscribe("FireSensor", 2);

            } catch (MqttException e) {
                e.printStackTrace();
            }

            // client.subscribe(new
            // String[]{"home/outsidetemperature","home/livingroomtemperature"});
            // client.subscribe("home/#");
            System.out.println("Hello!");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
