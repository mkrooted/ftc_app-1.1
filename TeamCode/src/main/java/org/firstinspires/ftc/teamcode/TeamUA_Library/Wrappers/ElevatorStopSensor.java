package org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayList;

/**
 * Created by mkrooted on 23.06.2017.
 */

public class ElevatorStopSensor {
    private LynxI2cColorRangeSensor sensor;
    private ArrayList<SensorEventListener> eventListenerList;

    private double MIN_DISTANCE;
    private double MAX_DISTANCE;

    boolean in_range;

    public ElevatorStopSensor(LynxI2cColorRangeSensor sensor, double min_distance, double max_distance) {
        this.sensor = sensor;
        eventListenerList = new ArrayList<>();
        MIN_DISTANCE = min_distance;
        MAX_DISTANCE = max_distance;
        in_range = false;
    }

    public void addEventListener(SensorEventListener event) {
        eventListenerList.add(event);
    }

    public double update() {
        double distance = sensor.getDistance(DistanceUnit.MM);

        if (distance >= MIN_DISTANCE && distance <= MAX_DISTANCE) {
            if (!in_range) {
                in_range = true;
                for (SensorEventListener event : eventListenerList) {
                    event.onElevatorSensorStateChanged(in_range);
                }
            }
        } else {
            if (in_range) {
                in_range = false;
                for (SensorEventListener event : eventListenerList) {
                    event.onElevatorSensorStateChanged(in_range);
                }
            }
        }

        return distance;
    }

    public boolean inRange() {
        return in_range;
    }

    public void clearEventListeners() {
        eventListenerList.clear();
    }

    public interface SensorEventListener {
        void onElevatorSensorStateChanged(boolean is_in_range);
    }
}
