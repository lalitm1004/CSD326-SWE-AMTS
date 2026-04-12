package org.amts.infrastructure.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.amts.domain.entities.seat.SeatType;
import org.amts.infrastructure.config.SeatLayoutProperties;
import org.amts.infrastructure.config.SeatLayoutProperties.RowConfig;
import org.amts.jooq.Tables;
import org.amts.jooq.enums.Seattypeenum;
import org.amts.jooq.tables.records.SeatRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class SeatLayoutLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final DSLContext dsl;
    private final SeatLayoutProperties properties;

    public SeatLayoutLoader(DSLContext dsl, SeatLayoutProperties properties) {
        this.dsl = dsl;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int existingCount = dsl.fetchCount(Tables.SEAT);
        if (existingCount > 0) {
            return;
        }

        List<SeatRecord> records = new ArrayList<>();

        for (RowConfig row : properties.getRows()) {
            Seattypeenum jooqType = row.getType() == SeatType.BALCONY
                    ? Seattypeenum.BALCONY
                    : Seattypeenum.ORDINARY;

            for (int i = 1; i <= row.getCount(); i++) {
                SeatRecord record = dsl.newRecord(Tables.SEAT);
                record.setId(UUID.randomUUID());
                record.setNumber(row.getLabel() + i);
                record.setType(jooqType);
                records.add(record);
            }
        }

        dsl.batchInsert(records).execute();
    }
}
