package bank.moneytransfersystem.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD("Доллар США"),
    EUR("Евро"),
    RUB("Российский рубль"),
    KGS("Кыргызский сом");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Currency fromDisplayName(String displayName) {
        for (Currency currency : values()) {
            if (currency.displayName.equals(displayName)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown display name: " + displayName);
    }

    public static Currency fromCode(String code) {
        try {
            return Currency.valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown currency code: " + code, e);
        }
    }
}
