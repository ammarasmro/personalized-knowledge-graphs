package draft_one;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {
	
	private String categoryLabel;
	private List<String> categoryStructure;
	
	public Category(String categoryLabel) {
		this.categoryLabel = categoryLabel;
		categoryStructure = new ArrayList<String>();
		labelIntoStructure();
	}

	private void labelIntoStructure() {
		for(String level: categoryLabel.split("/")) {
			if(level.length() > 0)
				categoryStructure.add(level);
		}
	}
	
	public List<String> getCategoryStructure() {
		return categoryStructure;
	}

	public void setCategoryStructure(List<String> categoryStructure) {
		this.categoryStructure = categoryStructure;
	}
	
	@Override
	public String toString() {
		String structure = "";
		
		for(String level: categoryStructure) {
			structure += String.format("- %s\n", level);
		}
		return String.format("%s\n%s", categoryLabel, structure);
	}

	public static void main(String[] args) {
		Category testCategory = new Category("/art and entertainment/visual art and design/design");
		System.out.println(testCategory);
		System.out.println(Arrays.toString("/art and entertainment/visual art and design/design".split("/")));
		
	}
}
