package dev.dotto.lifoapi.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

@Embeddable
@Data
public class UserItemId implements Serializable {
    private Long user;
    private Long item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserItemId that = (UserItemId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, item);
    }
}