package com.Digital.meal.tickets.demo.Entity;

import java.io.Serializable;
import java.util.Objects;

public class OttId implements Serializable {
    private String orderId;
    private String typeId;

    public OttId() {}

    public OttId(String orderId, String typeId) {
        this.orderId = orderId;
        this.typeId = typeId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OttId ottId = (OttId) o;
        return Objects.equals(orderId, ottId.orderId) && Objects.equals(typeId, ottId.typeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, typeId);
    }
}
