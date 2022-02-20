SYSC 3303: Iteration 2

Breakdown of Responsibilities – Iteration 1:

Ali Alvi:
Developed the Scheduler.java class.
Took part in code review sessions and approved outstanding pull requests.

Akaash Kapoor:
Developed the FloorSubsystem.java class.
Took part in code review sessions and approved outstanding pull requests.

Areeb Ul Haq:
Developed the ElevatorSubSystem.java class.
Took part in code review sessions and approved outstanding pull requests.

Hassan Jallad:
Developed unit tests to show the program reading the input file and passing the data between the different subsystems.
Took part in code review sessions and approved outstanding pull requests.

Raj Sandhu:
Developed UML diagram.
Developed Sequence diagram.
Wrote the README file.
Took part in code review sessions and approved outstanding pull requests.

Breakdown of Responsibilities – Iteration 2:

Ali Alvi:
Extended development for classes within the scheduler folder.
Developed the scheduler state machine.
Worked as a primary developer on scheduler and state machine diagram alongside Raj Sandhu.
Assisting in debugging general issues with the scheduler classes.
Took part in code review sessions and approved outstanding pull requests.

Akaash Kapoor:
Redesigned codebase to take advantage of the blocking queue data structure.
Performed debugging of code issues.
Assisted Raj with UML class diagram
Revised unit tests.
Took part in code review sessions and approved outstanding pull requests.

Areeb Ul Haq:
Extended unit test development.
Assisted in geral debugging of the program.
Assisted Raj in designing sequence diagram.
Took part in code review sessions and approved outstanding pull requests.

Hassan Jallad:
Extended development for the classes within the elevatorSubsystem folder.
Developed the elevator state machine.
Performed debugging of code issues.
Developed elevator state machine diagram.
Took part in code review sessions and approved outstanding pull requests.

Raj Sandhu:
Developed UML diagram.
Developed Sequence diagram.
Worked with Ali to develop state machine diagram.
Wrote the README file.
Assisted Ali with the classes within the scheduler folder, performing code cleanups and assisting in debugging.
Took part in code review sessions and approved outstanding pull requests.

File Names:
The source code to successfully run this project is located within the src folder. 
The classes representing the floor subsystem are located within the floorSubsystem folder (src/floorSubsystem) and are called FloorSubsystem.java, and FloorAttributes.java.
The class representing the elevator subsystem is located within the elevatorSubsystem folder (src/elevatorSubsystem) and is called ElevatorSubsystem.java.
The classes representing the elevator subsystem states is located within the ElevatorStates folder (src/elevatorSubsystem/ElevatorStates) and are called ElevatorState.java, MovingDown.java,
MovingUp.java, and Stationary.java.
The classes which store and receive text data, initialize the program to run (main method), and read text input are stored in the input folder (src/input) and are called inputBuffer.java, LaunchElevator.java, and Reader.java respectively.
The classes representing the scheduler subsystem are located within the scheduler folder (src/scheduler) and are called Scheduler.java, SchedulerRequest.java, SchedulerState.java, SchedulerStateMachine.java.
The input file that contains the test data to be read by the floor subsystem is located within the Inputs folder and is called test.txt.
The classes that contains the unit tests to test communication between the various subsystems, and to determine if state transitions occur as expected are located in the tests folder (src/tests) and are called InputTests.java, and StateTests.java.
The UML class diagram that shows the class hierarchy and relationships is located within the Documentation folder and is called uml-class-diagram.png.
The Sequence diagram that shows the interactions of the threads between the three subsystems is located within the Documentation folder and is called sequence-diagram.png.
The scheduler state machine diagram which shows the different states that it may take on is located within the Documentation folder and is called scheduler-state-machine-diagram.jpeg.
The elevator state machine diagram which shows the different states that it may take on is located within the Documentation folder and is called elevator-state-machine-diagram.jpeg.
This README file which outlines the individual responsibilities of the group members, information about file names, set up information, and test information is located within the Documentation folder and is called README.txt.

Set up Instructions:
Download the zip file submission from brightspace to a location of your choice.
Open up the Eclipse IDE.
Click File, Import, General, Projects from Folder or Archive, Next, then click Archive in the top right of the screen, find where you stored the zip file submission, select it, then hit finish.
Navigate to the LaunchElevator.java class (src/input/LaunchElevator.java) and hit run.

Testing Instructions:
Download the zip file submission from brightspace to a location of your choice.
Open up the Eclipse IDE.
Click File, Import, General, Projects from Folder or Archive, Next, then click Archive in the top right of the screen, find where you stored the zip file submission, select it, then hit finish.
Navigate to the InputTests.java file (src/tests/InputTests.java) and hit run.
Navigate to the StateTests.java file (src/tests/StateTests.java) and hit run.
