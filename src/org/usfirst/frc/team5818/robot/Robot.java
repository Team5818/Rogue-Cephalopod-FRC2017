
package org.usfirst.frc.team5818.robot;

import org.usfirst.frc.team5818.robot.commands.DriveForwardBackPID;
import org.usfirst.frc.team5818.robot.commands.DrivePIDDistance;
import org.usfirst.frc.team5818.robot.controllers.Driver;
import org.usfirst.frc.team5818.robot.subsystems.Collector;
import org.usfirst.frc.team5818.robot.subsystems.DriveTrain;
import org.usfirst.frc.team5818.robot.subsystems.DriveTrainSide;
import org.usfirst.frc.team5818.robot.subsystems.ExampleSubsystem;
import org.usfirst.frc.team5818.robot.subsystems.Turret;
import org.usfirst.frc.team5818.robot.subsystems.VisionTracker;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public DriveTrain driveTrain;
	public Driver driver;
	public Collector collector;
	public VisionTracker track;
	public Turret turret;
	public static Robot runningrobot;

    Command autonomousCommand;
    SendableChooser<Command> chooser;
    DriveTrainSide left;
    DriveTrainSide right;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	runningrobot = this;
    	driveTrain = new DriveTrain();
		track = new VisionTracker();
    	turret = new Turret();
    	collector = new Collector();
        chooser = new SendableChooser<>();
        chooser.addObject("Drive Forward", new DrivePIDDistance(72));
        chooser.addObject("Drive Forward Back", new DriveForwardBackPID(72, 6));
        SmartDashboard.putData("Auto mode", chooser);
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
        autonomousCommand = chooser.getSelected();
        
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new MyAutoCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} */
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        printSmartDash();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        driveTrain.getLeftSide().resetEnc();
        driveTrain.getRightSide().resetEnc();
        track.start();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	printSmartDash();
    	driver.teleopPeriodic();
        Scheduler.getInstance().run();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    public void printSmartDash(){
    	SmartDashboard.putNumber("Left in:", driveTrain.getLeftSide().getSidePosition());
    	SmartDashboard.putNumber("Right in:", driveTrain.getRightSide().getSidePosition());
    	SmartDashboard.putNumber("Gear X:", track.getCurrentX());
    	SmartDashboard.putNumber("Gear Y:", track.getCurrentY());
    	SmartDashboard.putNumber("Gear R:", track.getCurrentR());
    	SmartDashboard.putNumber("Turret Pot:", turret.getRawCounts());
    	SmartDashboard.putNumber("Turret Angle:", turret.getAng());
    	SmartDashboard.putNumber("Collector Angle:", collector.getAngle());
    }
}
