package org.usfirst.frc.team5818.robot.subsystems;

import org.usfirst.frc.team5818.robot.RobotMap;
import org.usfirst.frc.team5818.robot.constants.Side;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

/**
 * Subsystem representing one side of the drive base. Controlled with morbid
 * mathematician.
 */
public class DriveTrainSide {

    public static final double DIST_PER_REV = 4.0 * Math.PI;

    private CANTalon slaveTalon1;
    private CANTalon masterTalon;
    private CANTalon slaveTalon2;
    private Side mySide;

    public DriveTrainSide(Side side) {
        /* Instantiate different components depending on side */
        mySide = side;
        if (side == Side.CENTER) {
            throw new IllegalArgumentException("A drive side may not be in the center");
        }
        if (side == Side.RIGHT) {
            masterTalon = new CANTalon(RobotMap.R_TALON_ENC);
            slaveTalon1 = new CANTalon(RobotMap.R_TALON);
            slaveTalon2 = new CANTalon(RobotMap.R_TALON_2);

        } else {
            slaveTalon1 = new CANTalon(RobotMap.L_TALON);
            masterTalon = new CANTalon(RobotMap.L_TALON_ENC);
            slaveTalon2 = new CANTalon(RobotMap.L_TALON_2);
        }

        configTalons();

    }

    public void configTalons() {
        if (mySide == Side.RIGHT) {

            masterTalon.changeControlMode(TalonControlMode.PercentVbus);
            slaveTalon1.changeControlMode(TalonControlMode.Follower);
            slaveTalon2.changeControlMode(TalonControlMode.Follower);

            slaveTalon1.set(RobotMap.R_TALON_ENC);
            slaveTalon2.set(RobotMap.R_TALON_ENC);

            masterTalon.setInverted(true);
            slaveTalon1.reverseOutput(true);
            slaveTalon2.reverseOutput(true);

            masterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            masterTalon.configEncoderCodesPerRev(96);

            masterTalon.setF(1.07 * 1023.0 / 460.8);
            masterTalon.setP(8.0 * 1023.0 / 1000.0);
            masterTalon.setI(0.0);
            masterTalon.setD(100.0 * 1023.0 / 1000.0);
            masterTalon.setMotionMagicAcceleration(300.0);
            masterTalon.setMotionMagicCruiseVelocity(300.0);
            masterTalon.changeControlMode(TalonControlMode.MotionMagic);

        } else {

            masterTalon.changeControlMode(TalonControlMode.PercentVbus);
            slaveTalon1.changeControlMode(TalonControlMode.Follower);
            slaveTalon2.changeControlMode(TalonControlMode.Follower);

            slaveTalon1.set(RobotMap.L_TALON_ENC);
            slaveTalon2.set(RobotMap.L_TALON_ENC);

            masterTalon.setInverted(true);
            masterTalon.reverseSensor(true);
            slaveTalon1.reverseOutput(true);
            slaveTalon2.reverseOutput(false);

            masterTalon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            masterTalon.configEncoderCodesPerRev(96);

            masterTalon.setF(1.02 * 1.07 * 1023.0 / 460.8);
            masterTalon.setP(8.0 * 1023.0 / 1000.0);
            masterTalon.setI(0.0);
            masterTalon.setD(100.0 * 1023.0 / 1000.0);
            masterTalon.setMotionMagicAcceleration(300.0);
            masterTalon.setMotionMagicCruiseVelocity(300.0);
            masterTalon.changeControlMode(TalonControlMode.MotionMagic);
        }

    }

    public void slaveToOtherSide(boolean inverted) {
        if (mySide == Side.RIGHT) {
            masterTalon.changeControlMode(TalonControlMode.Follower);
            masterTalon.set(RobotMap.L_TALON_ENC);
            masterTalon.reverseOutput(inverted);
        } else {
            masterTalon.changeControlMode(TalonControlMode.Follower);
            masterTalon.set(RobotMap.R_TALON_ENC);
            masterTalon.reverseOutput(inverted);
        }
    }

    public void setPower(double numIn) {
        masterTalon.changeControlMode(TalonControlMode.PercentVbus);
        masterTalon.set(numIn);
    }

    public double getCruiseVel() {
        return masterTalon.getMotionMagicCruiseVelocity();
    }

    public double getTargetVel() {
        return masterTalon.getMotionMagicActTrajVelocity();
    }

    public void setCruiseVel(double vel) {
        masterTalon.setMotionMagicCruiseVelocity(vel);
    }

    public int getSideError() {
        return masterTalon.getClosedLoopError();
    }

    public void positionControl() {
        masterTalon.changeControlMode(CANTalon.TalonControlMode.MotionMagic);
    }

    public double getRevs() {
        return masterTalon.getPosition();
    }

    public void driveDistance(double dist) {
        double revs = dist / DIST_PER_REV;
        resetEnc();
        masterTalon.clearMotionProfileTrajectories();
        masterTalon.changeControlMode(TalonControlMode.MotionMagic);
        masterTalon.set(revs);
    }

    public void driveDistanceNoReset(double dist) {
        double revs = dist / DIST_PER_REV;
        masterTalon.set(revs);
    }

    public void driveDistanceNoReset(double dist, double acc, double vel) {
        masterTalon.setMotionMagicCruiseVelocity(vel);
        masterTalon.setMotionMagicAcceleration(acc);
        driveDistanceNoReset(dist);
    }

    public double getSidePosition() {
        return masterTalon.getPosition() * DIST_PER_REV;
    }

    public double getRawPos() {
        return masterTalon.getEncPosition();
    }

    public double getRawSpeed() {
        return masterTalon.getEncVelocity();
    }

    public double getSideVelocity() {
        return masterTalon.getSpeed();
    }

    public void resetEnc() {
        masterTalon.setEncPosition(0);
    }

    public void setCoastMode() {
        slaveTalon1.enableBrakeMode(false);
        masterTalon.enableBrakeMode(false);
        slaveTalon2.enableBrakeMode(false);
    }

    public void setBrakeMode() {
        masterTalon.enableBrakeMode(true);
        slaveTalon1.enableBrakeMode(true);
        slaveTalon2.enableBrakeMode(true);
    }
}
