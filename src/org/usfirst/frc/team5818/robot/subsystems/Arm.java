package org.usfirst.frc.team5818.robot.subsystems;

import org.usfirst.frc.team5818.robot.RobotMap;
import org.usfirst.frc.team5818.robot.utils.BetterPIDController;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm extends Subsystem implements PIDSource, PIDOutput {

    /**
     * Subsystem for robot's arm. Has 2 775s and a Vex absolute encoder.
     * Uses PD positioning control. 
     */
    
    /*PID Constants*/
    private static final double kP = 0.0006;
    private static final double kI = 0.0000;
    private static final double kD = 0.000045;

    /*Important positions and angles*/
    private static final double COLLECT_ANGLE = 11;
    public static final double COLLECT_POSITION = -1983;
    public static final double CLIMB_POSITION = -415;
    public static final double MID_POSITION = -233;
    public static final double NINETY_DEGREES = 26;
    public static final double SLOT_COLLECT_POSITION = NINETY_DEGREES;
    public static final double TURRET_RESET_POSITION = NINETY_DEGREES;
    public static final double LOAD_POSITION = 1000;
    
    /*Calculate scales and offsets*/
    public static final double ANGLE_SCALE = (90 - COLLECT_ANGLE) / (NINETY_DEGREES - COLLECT_POSITION);
    public static final double ANGLE_OFFSET = (COLLECT_ANGLE - (COLLECT_POSITION * ANGLE_SCALE)) - 16.3;
    
    /*Minimum power to keep in from falling at collect angle*/
    public static final double HOLD_POWER = .055;
    
    /*soft limits on arm position*/
    private double limitLow = COLLECT_POSITION;
    private double limitHigh = LOAD_POSITION;

    /*Talons + PID stuff*/
    private CANTalon leftMotorTal;
    private CANTalon rightMotorTal;
    public PIDSourceType pidType = PIDSourceType.kDisplacement;
    public BetterPIDController anglePID;


    public Arm() {
        leftMotorTal = new CANTalon(RobotMap.ARM_TALON_L);
        leftMotorTal.setInverted(false);
        rightMotorTal = new CANTalon(RobotMap.ARM_TALON_R);
        rightMotorTal.setInverted(true);
        /*use absolute encoder for an absolute position*/
        rightMotorTal.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
        anglePID = new BetterPIDController(kP, kI, kD, this, this);
        anglePID.setAbsoluteTolerance(0.3);
        setBrakeMode(true);
    }

    public void setBrakeMode(boolean mode) {
        leftMotorTal.enableBrakeMode(mode);
        rightMotorTal.enableBrakeMode(mode);
    }

    public void setPower(double x) {
        if (anglePID.isEnabled()) {
            anglePID.disable();
        }
        pidWrite(x);
        SmartDashboard.putNumber("Arn Power", x);
    }

    public void setAngle(double angle) {
        anglePID.disable();
        anglePID.setSetpoint(angle);
        anglePID.enable();
    }

    public double getPosition() {
        double pos = rightMotorTal.getPulseWidthPosition();
        /*If arm is outside of possible range, then encoder has wrapped*/
        if (pos > 1500) {
            return pos - 4096;
        }
        return pos;
    }

    public BetterPIDController getAnglePID() {
        return anglePID;
    }

    public void stop() {
        if (anglePID.isEnabled()) {
            anglePID.disable();
        }
        setBrakeMode(true);
        leftMotorTal.set(0);
        rightMotorTal.set(0);
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        pidType = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return pidType;
    }

    @Override
    public double pidGet() {
        return getPosition();
    }

    public double getIdlePower() {
        /*calculate the power needed to keep the arm still at the current angle*/
        SmartDashboard.putNumber("Arm Angle", Math.toRadians((getPosition() * ANGLE_SCALE + ANGLE_OFFSET)));
        return HOLD_POWER * Math.cos(Math.toRadians((getPosition() * ANGLE_SCALE + ANGLE_OFFSET)));
    }

    public void setLimitLow(double limitLow) {
        this.limitLow = limitLow;
    }

    public void setLimitHigh(double limitHigh) {
        this.limitHigh = limitHigh;
    }

    @Override
    public void pidWrite(double x) {
        /*Obey soft limits*/
        if (getPosition() <= limitLow) {
            x = Math.max(x, 0);
        } else if (getPosition() >= limitHigh) {
            x = Math.min(x, 0);
        }
        /*add idle power to prevent arm from falling*/
        leftMotorTal.set(x + getIdlePower());
        rightMotorTal.set(x + getIdlePower());
        SmartDashboard.putNumber("Arm Power", x);

    }

    @Override
    protected void initDefaultCommand() {
    }

}