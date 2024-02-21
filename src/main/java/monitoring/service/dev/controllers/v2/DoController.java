package monitoring.service.dev.controllers.v2;

import monitoring.service.dev.controllers.v2.impl.ImplDoController;
import monitoring.service.dev.services.logic.DoLogicService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/readings")
public class DoController extends ImplDoController {

    public DoController(DoLogicService service){
        super(service);
    }
}
