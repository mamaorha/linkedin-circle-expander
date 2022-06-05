package co.il.nmh.linkedin.circle.expander.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Filter {
    private final String value;
    private boolean include;

    public Filter(@JsonProperty("value") String value, @JsonProperty("include") boolean include) {
        this.value = value;
        this.include = include;
    }

    public String getValue() {
        return value;
    }

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(value, filter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
