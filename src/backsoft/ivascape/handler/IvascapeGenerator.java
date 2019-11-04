package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import backsoft.ivascape.viewcontrol.ViewUpdater;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class IvascapeGenerator {

    private static String[] adjectives = {"Super", "Military", "Cool", "Unique", "Authentic", "Thei Pye's", "Old", "",
            "Purple", "Hidden", "Golden", "Micro", "Nova", "", "Future", "High", "Total", "African", "Semi-Auto",
            "Underwater", "", "Fun&Playful" ,"Domestic", "International", "Daniel's", "Mocabo", "Chi Xuan Gin's"};
    private static String[] nouns = {"Electronics", "Elevators", "Education", "Beer Cups", "Soda Drinks", "Symbol",
            "Laboratories", "Pancakes", "Ciruit Machines", "Medical Assurance", "Automobiles", "Bakery",
            "Stainless Forks", "Bowling Club", "Cell Mobiles", "Girls", "Tubes&Consoles", "Transport", "Skating",
            "Instruments"};
    private static String[] suffices = {"","","", " inc.","","", " creators", "","","","",""," infinity",
            "","", " 2000", "","",""};

    public static void generate(int amount){
        Project project = Project.get();
        project.erase();
        ViewUpdater.current().updateAll();

        Random r = new Random();
        for (int i = 0; i < amount; i++) {

            int adj = r.nextInt(adjectives.length);
            int non = r.nextInt(nouns.length);
            int suf = r.nextInt(suffices.length);
            String title = adjectives[adj] + " " + nouns[non] + suffices[suf];
            project.add(new Company(title,"null", 0, LocalDate.now()));
        }
        int realAmount = project.companiesAmountProperty().getValue();
        List<String> list = project.getCompaniesTitlesList();

        int lAm = r.nextInt(realAmount * realAmount / 4);
        for (int i = 0; i < lAm; i++) {
            int a = r.nextInt(list.size());
            int b = r.nextInt(list.size());
            if (a == b){
                System.out.println("luck");
            }
            project.add(list.get(a), list.get(b), 100.0 + r.nextInt(1000));
        }
    }
}
