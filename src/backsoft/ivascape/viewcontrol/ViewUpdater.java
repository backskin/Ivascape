package backsoft.ivascape.viewcontrol;

public class ViewUpdater {

    private static ViewUpdater viewUpdater = null;
    public static ViewUpdater current(){
        return viewUpdater == null ? new ViewUpdater() : viewUpdater;
    }

    public ViewUpdater put(RootLayoutController c0) {
        RLController = c0;
        return this;
    }

    public ViewUpdater put(CompaniesViewController c0) {
        CVController = c0;
        return this;
    }

    public ViewUpdater put(LinksViewController c0) {
        LVController = c0;
        return this;
    }

    public ViewUpdater put(MapViewController c0) {
        MVController = c0;
        return this;
    }

    private RootLayoutController RLController;

    private CompaniesViewController CVController;

    private LinksViewController LVController;

    private MapViewController MVController;

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
