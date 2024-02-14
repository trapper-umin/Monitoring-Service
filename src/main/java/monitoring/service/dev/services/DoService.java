package monitoring.service.dev.services;

import java.util.List;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.IReadingsRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import monitoring.service.dev.utils.mappers.SensorListMapper;
import monitoring.service.dev.utils.validations.MeterReadingIndicationValidation;
import org.mapstruct.factory.Mappers;

public class DoService {

    private final static MeterReadingIndicationValidation indicationValidation = MeterReadingIndicationValidation.getInstance();
    private final static SensorListMapper mapperForSensorList = Mappers.getMapper(
        SensorListMapper.class);
    private final static PersonMapper mapperForPerson = Mappers.getMapper(PersonMapper.class);
    private final IPeopleRepository peopleRepository;
    private final IReadingsRepository readingRepository;

    public DoService(IPeopleRepository peopleRepository, IReadingsRepository readingRepository) {
        this.peopleRepository = peopleRepository;
        this.readingRepository = readingRepository;
    }

    public List<SensorDTO> getCurrentReadings(CredentialsDTOReqst credentials) {
        Person person = peopleRepository.findByUsername(credentials.getUsername()).orElseThrow(
            () -> new NotFoundException(
                "user with username '" + credentials.getUsername() + "' was not found"));

        List<Sensor> currentReadings = readingRepository.getCurrentReadings(person);

        return mapperForSensorList.convertToSensorDTOList(currentReadings);
    }

    public List<SensorDTO> getMonthlyReadings(CredentialsDTOReqst credentials, String month,
        String year) {
        Person person = peopleRepository.findByUsername(credentials.getUsername()).orElseThrow(
            () -> new NotFoundException(
                "user with username '" + credentials.getUsername() + "' was not found"));

        if (!month.matches("[A-Z][a-z]+")) {
            throw new NotValidException(
                "The month does not match the required pattern (e.g., 'January').");
        }
        try {
            if (Integer.parseInt(year) < AppConstants.MIN_YEAR_BORDER
                || Integer.parseInt(year) > AppConstants.MAX_YEAR_BORDER) {
                throw new NotValidException("The year should be between 1900 and 2500");
            }
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("Some parameters are empty");
        }

        List<Sensor> monthlyReadings = readingRepository.getMonthlyReadings(person, month, year);

        return mapperForSensorList.convertToSensorDTOList(monthlyReadings);
    }

    public void submitReading(CredentialsDTOWithSensorReqst credentials) {
        indicationValidation.valid(credentials.getSensor().getReading());

        readingRepository.submitReading(mapperForPerson.convertToPerson(credentials));
    }
}
