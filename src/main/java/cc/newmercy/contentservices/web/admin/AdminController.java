package cc.newmercy.contentservices.web.admin;

import java.util.List;
import java.util.Objects;

import cc.newmercy.contentservices.ServerStopper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final SermonSeriesInfoRepository sermonSeriesRepo;

    private final ServerStopper serverStopper;

    public AdminController(SermonSeriesInfoRepository sermonSeriesRepo, ServerStopper serverStopper) {
        this.sermonSeriesRepo = Objects.requireNonNull(sermonSeriesRepo, "sermon series repo");
        this.serverStopper = Objects.requireNonNull(serverStopper, "server stopper");
    }

    @RequestMapping(value = "sermon-series", method = RequestMethod.GET)
    @ResponseBody
    public List<SermonSeriesInfo> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return sermonSeriesRepo.list(page, pageSize);
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ResponseEntity<?> stop() throws Exception {
        serverStopper.stopServer();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
