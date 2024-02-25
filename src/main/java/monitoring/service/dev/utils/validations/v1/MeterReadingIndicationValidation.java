package monitoring.service.dev.utils.validations.v1;

import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.common.IValidator;
import org.springframework.stereotype.Component;

@Component
public class MeterReadingIndicationValidation implements IValidator<ReadingDTO> {

    @Override
    public void valid(ReadingDTO meterReading) {
        if (meterReading.getIndication() < 0) {
            throw new NotValidException("Indication should be greater than zero or equal");
        }
    }
}