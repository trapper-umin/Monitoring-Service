package monitoring.service.dev.services;

import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.RepositoryFactory;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.mappers.*;
import monitoring.service.dev.utils.validations.MeterReadingIndicationValidation;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class DoService {

    private final static IPeopleRepository repository = RepositoryFactory.getRepository();
    private final static MeterReadingIndicationValidation mriValidation = MeterReadingIndicationValidation.getInstance();
    private final static SensorListMapper mapperForSensorList = Mappers.getMapper(SensorListMapper.class);
    private final static PersonMapper mapperForPerson = Mappers.getMapper(PersonMapper.class);

    private static DoService instance;

    private DoService(){}

    public static DoService getInstance(){
        if(instance==null){
            instance = new DoService();
        }
        return instance;
    }

    public List<SensorDTO> getCurrentReadings(CredentialsDTO credentials){
        Person person = repository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException("user with username '"+credentials.getUsername()+"' was not found"));

        List<Sensor> currentReadings = repository.getCurrentReadings(person);

        return mapperForSensorList.convertToSensorDTOList(currentReadings);
    }

    public List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month, String year){
        Person person = repository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException("user with username '"+credentials.getUsername()+"' was not found"));

        if (!month.matches("[A-Z][a-z]+")) {
            throw new NotValidException("The month does not match the required pattern (e.g., 'January').");
        }

        if(Integer.parseInt(year)< AppConstants.MIN_YEAR_BORDER
                || Integer.parseInt(year) > AppConstants.MAX_YEAR_BORDER){
            throw new NotValidException("The year should be between 1900 and 2500");
        }

        List<Sensor> monthlyReadings = repository.getMonthlyReadings(person, month, year);

        return mapperForSensorList.convertToSensorDTOList(monthlyReadings);
    }

    public void submitReading(CredentialsDTO credentials) {
        mriValidation.valid(credentials.getSensors().get(0).getReadings().get(0));

        repository.submitReading(mapperForPerson.convertToPerson(credentials));
    }
}
