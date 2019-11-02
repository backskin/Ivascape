package backsoft.ivascape.viewcontrol;

public class ViewUpdater {

    private static ViewUpdater viewUpdater;
    public static ViewUpdater current(){
        viewUpdater = viewUpdater == null ? new ViewUpdater() : viewUpdater;
        return viewUpdater;
    }

    private ViewController RLController;
    private ViewController CVController;
    private ViewController LVController;
    private ViewController MVController;

    public static void putRootController(ViewController controller) {
        viewUpdater.RLController = controller;
    }

    static void putTabControllers(
            ViewController companiesViewController,
            ViewController linksViewController,
            ViewController mapViewController) {

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

    public ViewUpdater updateGraphView() {
        ((MapViewController) MVController).updateGraphView();
        return this;
    }

    void updateAll() {
        CVController.updateView();
        LVController.updateView();
        MVController.updateView();
        RLController.updateView();
    }
}
