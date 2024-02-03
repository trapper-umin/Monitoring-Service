package monitoring.service.dev.utils.validations.common;

import monitoring.service.dev.dtos.common.CommonDTO;

public interface IValidator<T extends CommonDTO> {

    void valid(T dto);
}