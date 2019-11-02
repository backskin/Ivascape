package backsoft.ivascape.viewcontrol;

public class ViewUpdater {

    private static ViewUpdater viewUpdater;
    public static ViewUpdater current(){
        viewUpdater = viewUpdater == null ? new ViewUpdater() : viewUpdater;
        return viewUpdater;
    }

    private ViewController CVController;
    private ViewController LVController;
    private ViewController MVController;

    void putTabControllers(
            ViewController companiesViewController,
            ViewController linksViewController,
            ViewController mapViewController) {

        CVController = companiesViewController;
        LVController = linksViewController;
        MVController = mapViewController;
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
        return ((MapViewController) MVController).getGVController();
    }

    void updateAll() {
        CVController.updateView();
        LVController.updateView();
        MVController.updateView();
    }
}
