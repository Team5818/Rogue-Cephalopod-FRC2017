package org.usfirst.frc.team5818.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FullExtention extends CommandGroup {

    public FullExtention(boolean on) {
        this.addSequential(new SetExtendTurret(on));
        this.addSequential(new SetPunchTurret(on));

    }

}
