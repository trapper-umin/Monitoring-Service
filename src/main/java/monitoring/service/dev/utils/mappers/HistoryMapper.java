package monitoring.service.dev.utils.mappers;

import java.util.List;
import monitoring.service.dev.dtos.responses.HistoryDTOResp;
import monitoring.service.dev.models.History;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    HistoryDTOResp convertToHistoryDTO(History history);
    List<HistoryDTOResp> convertToHistoryDTOList(List<History> histories);
}