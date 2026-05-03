package be.tanguy.beermap.api.common.meta;

import be.tanguy.beermap.api.common.meta.dto.ChangelogResponse;
import be.tanguy.beermap.api.common.meta.dto.MetaResponse;
import be.tanguy.beermap.api.common.web.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.Meta.BASE)
@RequiredArgsConstructor
public class MetaController {

    private final MetaProperties metaProperties;
    private final ChangelogProperties changelogProperties;

    @GetMapping
    public ResponseEntity<MetaResponse> getMeta() {
        return ResponseEntity.ok(new MetaResponse(metaProperties.version()));
    }

    @GetMapping(ApiPaths.Meta.CHANGELOG)
    public ResponseEntity<ChangelogResponse> getChangelog() {
        return ResponseEntity.ok(new ChangelogResponse(changelogProperties.entries()));
    }
}