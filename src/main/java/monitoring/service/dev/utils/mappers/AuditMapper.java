package monitoring.service.dev.utils.mappers;

import java.util.List;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.models.Audit;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface AuditMapper {
    List<AuditDTOResp> convertToAuditDTOList(List<Audit> audits);
}