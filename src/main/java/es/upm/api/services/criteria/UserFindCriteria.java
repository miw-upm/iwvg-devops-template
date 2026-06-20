package es.upm.api.services.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindCriteria {
    private Boolean active;
    private String mobile;

    public boolean all() {
        return active == null && (mobile == null || mobile.isBlank());
    }

    public boolean hasMobile() {
        return mobile != null && !mobile.isBlank();
    }
}
