package it.univaq.disim.se4as.paho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class ElectricitySensor {
	MqttClient client;

	public ElectricitySensor() {
		try {
			client = new MqttClient("tcp://127.0.0.1:1883", "pahomqttpublish1");
			client.connect();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		ElectricitySensor sensor = new ElectricitySensor();
		sensor.energyPublisher();
		
	}

	public Map<String, String> energyPublisher() {
		Map<String, String> consumptionMessage = new HashMap<String, String>();
		String delimiter = ",";
		String csvFile = "C:/Users/ASUS/Desktop/Erasmus Masters/IoT/project/energy_data/household_data_60min_singleindex.csv";
		try {
			File file = new File(csvFile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] tempArr;
			String consumption;
			String timestamp;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (!firstLine) {

					tempArr = line.split(delimiter);
					timestamp = tempArr[0];
					consumption = tempArr[tempArr.length - 1];
					// consumption = Float.valueOf(tempArr[tempArr.length-1]);
					System.out.print("time stamp:" + timestamp + "- consumption:" + consumption);
					consumptionMessage.put("Date", timestamp);
					consumptionMessage.put("Consumption", consumption);
					String consumptionMessageString = consumptionMessage.toString();
					try {

						MqttMessage message = new MqttMessage();
						message.setPayload(consumptionMessageString.getBytes());
						client.publish("SmartMeter/electricity", message);
						System.out.println("publisheeddd");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						// client.disconnect();
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}else{
					firstLine = false;
				}

			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return consumptionMessage;
	}

	
}
