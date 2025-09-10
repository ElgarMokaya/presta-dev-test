package com.elgar.walletsystem.mapping;

import com.elgar.walletsystem.model.ReconciliationItem;
import com.elgar.walletsystem.model.ReconciliationSummary;
import com.elgar.walletsystem.dto.response.ReconciliationItemResponse;
import com.elgar.walletsystem.dto.response.ReconciliationSummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReconciliationMapper {
    //ReconciliationMapper INSTANCE = Mappers.getMapper(ReconciliationMapper.class);

    ReconciliationItemResponse toItemResponse(ReconciliationItem entity);

    ReconciliationSummaryResponse toSummaryResponse(ReconciliationSummary entity);

}
