package konogonka;

import konogonka.Controllers.MainController;

public class MediatorControl {
    private MainController applicationController;

    public static MediatorControl getInstance(){
        return MediatorControlHold.INSTANCE;
    }

    private static class MediatorControlHold {
        private static final MediatorControl INSTANCE = new MediatorControl();
    }
    public void setController(MainController controller){
        this.applicationController = controller;
    }
    public MainController getContoller(){ return this.applicationController; }
}
