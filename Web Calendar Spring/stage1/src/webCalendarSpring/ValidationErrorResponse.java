package webCalendarSpring;

import java.util.List;

public class ValidationErrorResponse {

    public ValidationErrorResponse(List<Violation> violations) {
        this.violations = violations;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    private final List<Violation> violations;

}
