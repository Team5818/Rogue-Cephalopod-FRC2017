package org.usfirst.frc.team5818.robot.subsystems;

import static org.usfirst.frc.team5818.robot.constants.Constants.Constant;

import org.usfirst.frc.team5818.robot.RobotMap;
import org.usfirst.frc.team5818.robot.constants.Side;
import org.usfirst.frc.team5818.robot.utils.BetterPIDController;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveTrainSide extends Subsystem implements PIDSource, PIDOutput {

    /**
     * Subsystem representing one side of the drive base. Capable of velocity or
     * distance-based PID control.
     */
    
    public static final double VEL_KP = 0.015; 
    public static final double VEL_KI = 0.0; 
    public static final double VEL_KD = 0.10;
    public static final double VEL_KF = 0.025; 

    public static final double DIST_KP = 0.005;
    public static final double DIST_KI = 0.0001; 
    public static final double DIST_KD = 0.0; 

    public static final double LEFT_ENC_SCALE = Constant.encoderScale();
    public static final double RIGHT_ENC_SCALE = Constant.encoderScale();

    private CANTalon motorNoEnc;
    private CANTalon motorEnc;
    private CANTalon motor2NoEnc;
    private Encoder enc;

    public BetterPIDController distController;
    public BetterPIDController velController;

    public PIDSourceType pidType = PIDSourceType.kDisplacement;

    public DriveTrainSide(Side side) {
        /*Instantiate different components depending on side*/
        if (side == Side.CENTER) {
            throw new IllegalArgumentException("A drive side may not be in the center");
        }
        if (side == Side.RIGHT) {
            motorNoEnc = new CANTalon(RobotMap.L_TALON);
            motorEnc = new CANTalon(RobotMap.L_TALON_ENC);
            motor2NoEnc = new CANTalon(RobotMap.L_TALON_2);
            motorNoEnc.setInverted(false);
            motorEnc.setInverted(true);
            motor2NoEnc.setInverted(false);
            velController = new BetterPIDController(VEL_KP, VEL_KI, VEL_KD, VEL_KF, this, this);
            distController = new BetterPIDController(DIST_KP, DIST_KI, DIST_KD, this, this);
            enc = new Encoder(9, 8, true, Encoder.EncodingType.k4X);
            enc.setDistancePerPulse(LEFT_ENC_SCALE);
            enc.setMaxPeriod(0.1);
        } else {
            motorNoEnc = new CANTalon(RobotMap.R_TALON);
            motorEnc = new CANTalon(RobotMap.R_TALON_ENC);
            motor2NoEnc = new CANTalon(RobotMap.R_TALON_2);
            motorNoEnc.setInverted(true);
            motorEnc.setInverted(false);
            motor2NoEnc.setInverted(true);
            velController = new BetterPIDController(VEL_KP, VEL_KI, VEL_KD, VEL_KF, this, this);
            distController = new BetterPIDController(DIST_KP, DIST_KI, DIST_KD, this, this);
            enc = new Encoder(7, 6, false, Encoder.EncodingType.k4X);
            enc.setDistancePerPulse(RIGHT_ENC_SCALE);
            enc.setMaxPeriod(0.1);
        }
        distController.setAbsoluteTolerance(1);

    }

    public void setPower(double numIn) {
        if (velController.isEnabled()) {
            velController.disable();
        }
        if (distController.isEnabled()) {
            distController.disable();
        }
        motorNoEnc.set(numIn);
        motorEnc.set(numIn);
        motor2NoEnc.set(numIn);
    }

    public double getSidePosition() {
        return enc.getDistance();
    }

    public double getRawPos() {
        return enc.getRaw();
    }

    public double getSideVelocity() {
        if (enc.getStopped())
            return 0;
        else
            return enc.getRate();
    }

    @Override
    public void pidWrite(double val) {
        motorEnc.set(val);
        motorNoEnc.set(val);
        motor2NoEnc.set(val);
    }

    @Override
    public double pidGet() {
        /*Return position or velocity depending on current control type*/
        if (pidType == PIDSourceType.kDisplacement) {
            return getSidePosition();
        } else {
            return motorEnc.getEncVelocity();
        }
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        pidType = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return pidType;
    }

    public void driveDistance(double dist) {
        pidType = PIDSourceType.kDisplacement;
        velController.disable();
        distController.disable();
        resetEnc();
        distController.setSetpoint(dist);
        distController.enable();
    }

    public void driveVelocity(double dist) {
        pidType = PIDSourceType.kRate;
        velController.disable();
        distController.disable();
        resetEnc();
        velController.setSetpoint(dist);
        velController.enable();
    }

    public BetterPIDController getVelController() {
        return velController;
    }

    public BetterPIDController getDistController() {
        return distController;
    }

    public void resetEnc() {
        enc.reset();
    }

    public void setCoastMode() {
        motorNoEnc.enableBrakeMode(false);
        motorEnc.enableBrakeMode(false);
        motor2NoEnc.enableBrakeMode(false);
    }

    public void setBrakeMode() {
        motorEnc.enableBrakeMode(true);
        motorNoEnc.enableBrakeMode(true);
        motor2NoEnc.enableBrakeMode(true);
    }

    @Override
    public void initDefaultCommand() {

    }
}
