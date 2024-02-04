package monitoring.service.dev.utils.validations;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.common.CommonDTO;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.common.IValidator;

public class MeterReadingIndicationValidation implements IValidator<MeterReadingDTO> {

    private static MeterReadingIndicationValidation instance;

    private MeterReadingIndicationValidation(){}

    public static MeterReadingIndicationValidation getInstance(){
        if(instance==null){
            instance = new MeterReadingIndicationValidation();
        }
        return instance;
    }

    @Override
    public void valid(MeterReadingDTO meterReading) {
        if(meterReading.getIndication()<0){
            throw new NotValidException("Indication should be greater than zero or equal");
        }
    }
}
