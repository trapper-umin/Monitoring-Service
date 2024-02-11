package monitoring.service.dev.controllers;

import monitoring.service.dev.controllers.impl.ImplAdminController;

public class AdminController extends ImplAdminController {

    private static AdminController instance;

    private AdminController() {
    }

    public static AdminController getInstance() {
        if (instance == null) {
            instance = new AdminController();
        }
        return instance;
    }
}
