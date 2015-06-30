package pimms.joakimvision.transport;

import java.util.List;

public interface TransportDelegate {
    void onTransportDataDownloaded(List<TransportDeparture> departures);
    void onTransportDataFailed(String errorMessage);
}
