package pimms.joakimvision.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransportDeparture {
    public enum VehicleType {
        UNDEFINED,
        TRAIN,
        BUS,
        TRAM,
        METRO,
    }

    private String _destinationStation;
    private Date _departure;
    private int _delayMinutes;
    private String _lineName;
    private List<String> _deviations = new ArrayList<>();
    private VehicleType _vehicleType = VehicleType.UNDEFINED;
    private String _platformName;

    public String getDestinationStation() {
        return _destinationStation;
    }

    public Date getDeparture() {
        return _departure;
    }

    public int getDelayMinutes() {
        return _delayMinutes;
    }

    public String getLineName() {
        return _lineName;
    }

    public List<String> getDeviations() {
        return _deviations;
    }

    public VehicleType getVehicleType() {
        return _vehicleType;
    }

    public String getPlatformName() {
        return _platformName;
    }


    void setDestinationStation(String destinationStation) {
        _destinationStation = destinationStation;
    }

    void setDelayMinutes(int delayMinutes) {
        _delayMinutes = delayMinutes;
    }

    void setLineName(String lineName) {
        _lineName = lineName;
    }

    void setDeparture(Date departure) {
        _departure = departure;
    }

    void addDeviation(String deviation) {
        _deviations.add(deviation);
    }

    void setVehicleType(VehicleType type) {
        _vehicleType = type;
    }

    void setPlatformName(String platformName) {
        _platformName = platformName;
    }
}
