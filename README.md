<!-- ## Simulation and Demonstration of Proposed Model of Cloud-IoT for Healthcare -->
## Cloud-Fog-Edge computing for Internet of Health Things
We have reviewed numerous research papers to identify the key areas of study needed in current Cloud-IoT models for improving the healthcare sector. Our research project aims to reduce network usage consumption and latency, thereby achieving a highly performant, fast, and cost-effective model. We have successfully demonstrated the feasibility of this proposed model in a real-life scenario.

> **Target audience:** Researchers and developers working on Cloud-IoT and healthcare applications.

**Note:** This repository includes the iFogSim simulation code and the actual demonstration code for programming microcontrollers.

------------

**Table of Contents:**

* **Installation**
    * Setting Up the Environment in iFogSim Simulation 
    * Running the Simulation
* **Microcontroller Setup**
    * Hardware Requirements
    * Circuit Connection Diagram
    * Programming the Microcontroller
* **Proposed Model**
* **Demonstration**
* **Outcome**

#### Installation:

###### Setting Up the Environment in iFogSim Simulation

1. Download the code from `simulation-fog-healthcare` folder of this repository in the zip file format.
2. Extract the contents and give appropriate name for this folder.
3. Download the latest updated version of Eclipse IDE.
4. Run executable Eclipse IDE file and follow the installation instructions.
5. Open Eclipse IDE, click file, select new and choose create new Java project.
6. Choose the folder which contains extracted files.

###### Running the Simulation

1. Now open the src folder go to the org.fog.test.perfeval package. This package stores the main code for simulation.
2. Open the file named `fogHealthMonitoring` and run over various testcases.

> **Note: **Make sure that JRE or JDK version 1.7 or more were already installed in the system.

#### Microcontroller Setup:
###### Hardware Requirements

| S.No.  | Part Name  | Quantity  | Cost (₹) |
| ------------ | ------------ | ------------ | ------------ |
| 1 | ESP8266 NodeMCU | 1 | 265 |
| 2 | Arduino Uno/Nano | 1 | 250 |
| 3 | MAX30100 PulseOximeter Sensor |1 | 120 |
| 4 | Wires | 1/2 m.| 10  |

###### Circuit Connection Diagram

<img src="https://github.com/ridamEXE/simulation-and-demonstration-of-proposed-model-of-Cloud-IoT-for-healthcare/assets/94859397/14190d6b-6166-4c93-b450-bb11bc77b1ab" width="600" height="370">

> Connection diagram of Proposed Model.

###### Programming the Microcontroller	
1. Download and install Arduino IDE, open the Arduino IDE after installation.
2. Select your board and choose the appropriate board type from the "Tools -> Board", matching your specific microcontroller.
3. Connect the microcontroller to the computer using a USB cable.
4. Open the code file: `cloud-iot-demo(arduino).ino` and `cloud-iot-demo(esp8266).ino` file.
5. Update the WiFi credentials (SSID and password) in the `arduino_secrets.h` file.
6. Upload the code.

#### Proposed Model:

<img src="https://github.com/ridamEXE/simulation-and-demonstration-of-proposed-model-of-Cloud-IoT-for-healthcare/assets/94859397/7d5055d5-06ae-407b-bc1f-3ce2b1c385c5" width="350" height="500">

> Proposed Architecture.

#### Demonstration:

<img src="https://github.com/ridamEXE/simulation-and-demonstration-of-proposed-model-of-Cloud-IoT-for-healthcare/assets/94859397/a69436b6-8dc6-4a03-b7a6-ddcbedb5a7bc" width="600" height="350">

> Set-up of Proposed Model.

#### Outcome:

<img src="https://github.com/ridamEXE/simulation-and-demonstration-of-proposed-model-of-Cloud-IoT-for-healthcare/assets/94859397/c45f1679-4b7b-40f9-aae4-2f4c7bc85020" width="235" height="500">

> Sensor data uploaded on cloud.