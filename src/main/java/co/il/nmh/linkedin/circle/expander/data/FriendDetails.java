package co.il.nmh.linkedin.circle.expander.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.openqa.selenium.WebElement;

/**
 * @author Maor Hamami
 */

@Data
@AllArgsConstructor
@ToString(exclude = {"webElement"})
public class FriendDetails {
    private WebElement webElement;
    private String name;
    private String description;
    private String insight;
}
