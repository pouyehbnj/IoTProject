package it.univaq.disim.se4as.paho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class SmartMeterSubscriber implements MqttCallback {

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

    if (topic.equals("SmartMeter/electricity")) {

      System.out.println(String.format(" received electricity: %s", messageText));
      String consumption = messageText.split(",")[0].split("=")[1];
      String date = messageText.split(",")[1].split("=")[1].split("}")[0];
      System.out.println("consumption is:"+consumption);
      System.out.println("Date is:"+date);


    } else if (topic.equals("SmartMeter/gas")) {

      System.out.println(String.format(" received gas: %s", messageText));

    } else if (topic.equals("SmartMeter/water")) {

      System.out.println(String.format(" received water: %s", messageText));
      String consumption = messageText.split(",")[0].split("=")[1];
      String date = messageText.split(",")[1].split("=")[1].split("}")[0];
      System.out.println("water consumption is:"+consumption);
      System.out.println("Date is:"+date);
    }

    // System.out.println("A new message arrived from the topic: \"" + arg0 + "\".
    // The payload of the message is " + message.toString());

  }

  public static void main(String[] args) {

    // new PahoDemoImplCallBack().publish1();
    new SmartMeterSubscriber().subscribe();
  }

  public void subscribe() {
    try {
      client = new MqttClient("tcp://localhost:1883", "SmartMeter");

      client.setCallback(this);
      client.connect();
      try {

        client.subscribe("SmartMeter/electricity", 2);
        client.subscribe("SmartMeter/gas", 2);
        client.subscribe("SmartMeter/water", 2);

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
