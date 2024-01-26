package monitoring.service.dev.services;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.Repository;
import monitoring.service.dev.repositories.RepositoryFactory;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.mappers.MeterReadingMapper;
import monitoring.service.dev.utils.mappers.SensorAndMeterReadingMapMapper;
import monitoring.service.dev.utils.mappers.SensorMapper;
import monitoring.service.dev.utils.validations.MeterReadingIndicationValidation;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

public class DoService {

    private final static IPeopleRepository repository = RepositoryFactory.getRepository();
    private final static MeterReadingIndicationValidation mriValidation = MeterReadingIndicationValidation.getInstance();
    private final static SensorAndMeterReadingMapMapper mapper = Mappers.getMapper(SensorAndMeterReadingMapMapper.class);
    private final static SensorMapper mapperForSensor = Mappers.getMapper(SensorMapper.class);
    private final static MeterReadingMapper mapperForReading = Mappers.getMapper(MeterReadingMapper.class);

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

        return mapper.convertToDtoMap(currentReadings);
    }

    public Map<SensorDTO, MeterReadingDTO> getMonthlyReadings(Person person, String month){

        return null;
    }

    public void submitReading(Person credentials, SensorDTO sensor, MeterReadingDTO reading) {
        mriValidation.valid(reading);

        repository.submitReading(credentials,
                mapperForSensor.convertToSensor(sensor),
                mapperForReading.convertToMeterReading(reading));
    }
}
