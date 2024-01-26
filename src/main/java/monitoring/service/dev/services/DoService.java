package monitoring.service.dev.services;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.Repository;
import monitoring.service.dev.utils.exceptions.NotFoundException;

import java.util.Map;

public class DoService {

    private final static Repository repository = Repository.getInstance();

    private static DoService instance;

    private DoService(){}

    public static DoService getInstance(){
        if(instance==null){
            instance = new DoService();
        }
        return instance;
    }

    public Map<SensorDTO, MeterReadingDTO> getCurrentReadings(Person credentials){
        repository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException("user with username '"+credentials.getUsername()+"' was not found"));

        Map<Sensor, MeterReading> currentReadings = repository.getCurrentReadings(credentials);
        return null;
        //TODO
    }

    public Map<SensorDTO, MeterReadingDTO> getMonthlyReadings(Person person, String month){

        return null;
    }

    public void submitReading(SensorDTO sensor) {

    }
}
