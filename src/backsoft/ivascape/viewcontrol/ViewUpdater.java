package backsoft.ivascape.viewcontrol;

public class ViewUpdater {

    private static ViewUpdater viewUpdater;

    public static ViewUpdater current(){ return viewUpdater; }

    private CompaniesViewController CVController;
    private LinksViewController LVController;
    private MapViewController MVController;

    static void putTabControllers(
            CompaniesViewController companiesViewController,
            LinksViewController linksViewController,
            MapViewController mapViewController) {

        viewUpdater = new ViewUpdater();
        viewUpdater.CVController = companiesViewController;
        viewUpdater.LVController = linksViewController;
        viewUpdater.MVController = mapViewController;
    }

    public ViewUpdater updateCompaniesView() {
        CVController.updateView();
        return this;
    }

    public ViewUpdater updateLinksView() {
        LVController.updateView();
        return this;
    }

    public GraphViewController getGVController(){
        return MVController.getGVController();
    }

    public void updateAll() {
        CVController.updateView();
        LVController.updateView();
        MVController.updateView();
    }
}
