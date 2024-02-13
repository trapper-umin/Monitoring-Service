package monitoring.service.dev.utils.mappers;

import java.util.List;
import monitoring.service.dev.dtos.requests.HistoryDTOReqst;
import monitoring.service.dev.models.History;
import org.mapstruct.Mapper;

@Mapper
public interface HistoryMapper {

    HistoryDTOReqst convertToHistoryDTO(History history);
    List<HistoryDTOReqst> convertToHistoryDTOList(List<History> histories);
}