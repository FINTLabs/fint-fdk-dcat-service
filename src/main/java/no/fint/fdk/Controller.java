package no.fint.fdk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.fint.portal.model.organisation.Organisation;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@Api(tags = "dcat-no-catalogs", description = "DCAT-NO Controller")
@CrossOrigin(origins = "*")
@RequestMapping(value = "/catalogs")
public class Controller {

    @Autowired
    private ModelService modelService;

    @ApiOperation(value = "Get Agent")
    @GetMapping(value = "/{orgNum}", produces = {"text/turtle"})
    public ResponseEntity<String> getOrganisationAsTurtle(@PathVariable final String orgNum) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(modelService.getOrganisationModel(organisation.get())));
        }

        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get Agent")
    @GetMapping(value = "/{orgNum}", produces = {"application/rdf+xml"})
    public ResponseEntity<String> getOrganisationAsXml(@PathVariable final String orgNum) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getXmlString(modelService.getOrganisationModel(organisation.get())));
        }

        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get Catalog")
    @GetMapping(value = "/{orgNum}/catalog", produces = {"text/turtle"})
    public ResponseEntity<String> getCatalogAsTurtle(@PathVariable final String orgNum) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(modelService.getCatalogModel(organisation.get())));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get Catalog")
    @GetMapping(value = "/{orgNum}/catalog", produces = {"application/rdf+xml"})
    public ResponseEntity<String> getCatalogAsXml(@PathVariable final String orgNum) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getXmlString(modelService.getCatalogModel(organisation.get())));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get dataset")
    @GetMapping(value = "/{orgNum}/dataset/{id}", produces = {"text/turtle"})
    public ResponseEntity<String> getDatasetAsTurtle(@PathVariable final String orgNum, @PathVariable final String id) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);
        Optional<Model> datasetModel = modelService.getDatasetModel(organisation.get(), id);

        if (organisation.isPresent() && datasetModel.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(datasetModel.get()));
        }

        return ResponseEntity.notFound().build();

    }

    @ApiOperation(value = "Get full FDK")
    @GetMapping(value = "/{orgNum}/fdk", produces = {"text/turtle"})
    public ResponseEntity<String> getFdkAsTurtle(@PathVariable final String orgNum) {

        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(modelService.getFdkModel(organisation.get())));
        }

        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get full FDK")
    @GetMapping(value = "/{orgNum}/fdk", produces = {"application/rdf+xml"})
    public ResponseEntity<String> getFdkAsXml(@PathVariable final String orgNum) {
        Optional<Organisation> organisation = modelService.getOrganisation(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(modelService.getFdkModel(organisation.get())));
        }

        return ResponseEntity.notFound().build();
    }


}
