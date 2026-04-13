package org.amts.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.amts.domain.entities.seat.SeatDesignation;
import org.amts.domain.entities.seat.SeatType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auditorium")
public class SeatLayoutProperties {

    private List<RowConfig> rows = new ArrayList<>();

    public List<RowConfig> getRows() {
        return rows;
    }

    public void setRows(List<RowConfig> rows) {
        this.rows = rows;
    }

    public static class RowConfig {
        private String label;
        private SeatType type;
        private int count;
        private SeatDesignation designation = SeatDesignation.ORDINARY;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public SeatType getType() {
            return type;
        }

        public void setType(SeatType type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public SeatDesignation getDesignation() {
            return designation;
        }

        public void setDesignation(SeatDesignation designation) {
            this.designation = designation;
        }
    }
}
