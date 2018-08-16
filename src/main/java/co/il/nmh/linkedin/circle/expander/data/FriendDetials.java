package co.il.nmh.linkedin.circle.expander.data;

import org.openqa.selenium.WebElement;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Maor Hamami
 */

@Data
@AllArgsConstructor
public class FriendDetials
{
	private WebElement webElement;
	private String name;
	private String description;
	private String insight;
}
