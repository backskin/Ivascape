package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class IvascapeGenerator {

    private static String[] adjectives = {"Super", "Cool", "Unique", "Authentic", "Old", "", "Purple", "Hidden",
            "Golden", "Micro", "Nova", "", "Future", "High", "Total", "Domestic", "International"};
    private static String[] nouns = {"Electronics", "Elevators", "Education", "Beer Cups", "Soda Drinks", "Symbol",
            "Laboratories", "Pancakes", "Ciruit Machines", "Automobiles", "Transport", "Skating",
            "Instruments"};
    private static String[] suffices = {"", "", "", " inc.", " creators", "", " infinity", "", "", " 2000", "", "", ""};

    public static void generate(int amount){
        Project project = Project.get();
        project.erase();
        Random r = new Random();
        for (int i = 0; i < amount; i++) {

            int adj = r.nextInt(adjectives.length);
            int non = r.nextInt(nouns.length);
            int suf = r.nextInt(suffices.length);
            String title = adjectives[adj] + " " + nouns[non] + suffices[suf];
            project.add(new Company(title,"null", 0, LocalDate.now()));
        }
        int realAmount = project.companiesAmountProperty().getValue();
        List<String> list = project.getCompaniesList();
        int lAm = r.nextInt(realAmount * realAmount / 2) / 2;
        for (int i = Math.min(1, realAmount-1); i < lAm; i++){
            project.add(
                    project.getCompany(list.get(r.nextInt(list.size()))),
                    project.getCompany(list.get(r.nextInt(list.size()))),
                    100.0 + r.nextInt(100));
        }
    }
}
