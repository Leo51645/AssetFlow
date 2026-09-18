package com.github.leo51645.assetflow.holdings.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class HoldingId implements Serializable {

    private Long userId;
    private Long assetId;

    protected HoldingId() {}

    public HoldingId(Long userId, Long assetId) {
        this.userId = userId;
        this.assetId = assetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoldingId)) return false;
        HoldingId that = (HoldingId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, assetId);
    }
}
