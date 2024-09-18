package bank.moneytransfersystem.enums;

import lombok.Getter;

@Getter
public enum Status {
    CREATED("СОЗДАН"),
    ISSUED("ВЫДАН");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}