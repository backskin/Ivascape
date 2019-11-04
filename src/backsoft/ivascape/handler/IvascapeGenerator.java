package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import backsoft.ivascape.viewcontrol.ViewUpdater;

import java.util.List;
import java.util.Random;

public class IvascapeGenerator {

    private static String[] adjectives = {"Super ", "Military ", "Cool ", "Unique ", "Authentic ", "Thei Pye's ", "Old ",
            "", "Purple ", "Hidden ", "Golden ", "Micro ", "Nova ", "La-La ", "Fairy ", "", "Future ", "High ", "Total ",
            "African ", "Semi-Auto ", "Underwater ", "", "Fun&Playful " ,"Domestic ", "International ", "Daniel's ",
            "Mocabo ", "Chi Xuan Gin's "};
    private static String[] nouns = {"Electronics", "Elevators", "Education", "Beer Cups", "Soda Drinks", "Symbol Typings",
            "Laboratories", "Pancakes", "Ciruits", "Skynet", "Street Cafe", "Medical Assurance", "Kitchen-Shmitchen",
            "Automobiles", "Bakery", "Stainless Forks", "Dance Club", "Bowling Club", "Cellphones", "Girls",
            "Tubes&Consoles", "Transport", "Skating", "Instruments"};
    private static String[] suffices = {"","","", " inc.","","", " creators", "", ""," bros.","",""," infinity", "","",
            " 2000", "","",""};

    public static void generate(int amount){
        Project project = Project.get();
        project.erase();
        ViewUpdater.current().updateAll();

        Random r = new Random();
        for (int i = 0; i < amount; i++) {

            int adj = r.nextInt(adjectives.length);
            int non = r.nextInt(nouns.length);
            int suf = r.nextInt(suffices.length);
            String title = adjectives[adj] + nouns[non] + suffices[suf];
            project.add(Company.createCompany().setTitle(title));
        }
        int realAmount = project.companiesAmountProperty().getValue();
        List<String> list = project.getCompaniesTitlesList();

        int lAm = r.nextInt(realAmount * realAmount) / 2;
        for (int i = 0; i < lAm; i++) {
            int a = r.nextInt(list.size());
            int b = r.nextInt(list.size());
            if (a != b) project.add(list.get(a), list.get(b), 100.0 + r.nextInt(1000));
        }
    }
}
