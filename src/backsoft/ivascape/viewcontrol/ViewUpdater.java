package backsoft.ivascape.viewcontrol;

public class ViewUpdater {

    private static ViewUpdater viewUpdater = null;
    public static ViewUpdater current(){
        return viewUpdater == null ? new ViewUpdater() : viewUpdater;
    }

    private RootLayoutController RLController;
    private CompaniesViewController CVController;
    private LinksViewController LVController;
    private MapViewController MVController;

    public static void putRootController(RootLayoutController controller) {
        viewUpdater.RLController = controller;
    }

    static void putTabControllers(
            CompaniesViewController companiesViewController,
            LinksViewController linksViewController,
            MapViewController mapViewController) {

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

    public ViewUpdater updateMapView() {
        MVController.updateView();
        return this;
    }

    public ViewUpdater updateGraphView() {
        MVController.getGVController().updateView();
        return this;
    }

    public void updateAll() {
        CVController.updateView();
        LVController.updateView();
        MVController.updateView();
        RLController.updateStatusbar();
    }
}
