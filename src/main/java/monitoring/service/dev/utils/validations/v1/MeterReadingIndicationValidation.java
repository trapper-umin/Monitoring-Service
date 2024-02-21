package monitoring.service.dev.utils.validations.v1;

import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.common.IValidator;

public class MeterReadingIndicationValidation implements IValidator<ReadingDTO> {

    private static MeterReadingIndicationValidation instance;

    private MeterReadingIndicationValidation() {
    }

    public static MeterReadingIndicationValidation getInstance() {
        if (instance == null) {
            instance = new MeterReadingIndicationValidation();
        }
        return instance;
    }

    @Override
    public void valid(ReadingDTO meterReading) {
        if (meterReading.getIndication() < 0) {
            throw new NotValidException("Indication should be greater than zero or equal");
        }
    }
}