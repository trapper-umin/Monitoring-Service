package monitoring.service.dev.controllers.v2;

import monitoring.service.dev.controllers.v2.impl.ImplAdminController;
import monitoring.service.dev.services.logic.AdminLogicService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/admins")
public class AdminController extends ImplAdminController {

    public AdminController(AdminLogicService service){
        super(service);
    }
}
