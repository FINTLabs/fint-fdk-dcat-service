package no.fint.fdk.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.fdk.*;
import no.fint.fdk.vocabulary.EuMetadataRegistry;
import no.fint.portal.model.component.Component;
import no.fint.portal.model.component.ComponentService;
import no.fint.portal.model.organisation.Organisation;
import no.fint.portal.model.organisation.OrganisationService;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Api(tags = "Catalogs")
@CrossOrigin(origins = "*")
@RequestMapping(value = "/catalogs")
public class DCATController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ComponentService componentService;

    @GetMapping(value = "/{orgNum}", produces = {"text/turtle"})
    public ResponseEntity<String> getOrganisationAsTurtle(@PathVariable final String orgNum) {

        Optional<Model> organisation = getOrganisationModel(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(organisation.get()));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{orgNum}", produces = {"application/rdf+xml"})
    public ResponseEntity<String> getOrganisationAsXml(@PathVariable final String orgNum) {

        Optional<Model> organisation = getOrganisationModel(orgNum);

        if (organisation.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getXmlString(organisation.get()));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{orgNum}/catalog", produces = {"text/turtle"})
    public ResponseEntity<String> getCatalogAsTurtle(@PathVariable final String orgNum) {
        Optional<Model> catalog = getCatalogModel(orgNum);

        if (catalog.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(catalog.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{orgNum}/catalog", produces = {"application/rdf+xml"})
    public ResponseEntity<String> getCatalogAsXml(@PathVariable final String orgNum) {
        Optional<Model> catalog = getCatalogModel(orgNum);

        if (catalog.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getXmlString(catalog.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{orgNum}/fdk", produces = {"text/turtle"})
    public ResponseEntity<String> getFdkAsTurtle(@PathVariable final String orgNum) {
        Optional<Model> fdkModel = getFdkModel(orgNum);

        if (fdkModel.isPresent()) {
            return ResponseEntity.ok(RdfUtilities.getTurtleString(fdkModel.get()));
        }

        return ResponseEntity.notFound().build();
    }

    private Optional<Model> getFdkModel(String orgNum) {
        Optional<Model> organisationModel = getOrganisationModel(orgNum);
        Optional<Model> catalogModel = getCatalogModel(orgNum);
        List<Model> datasetModels = getDatasetModels(orgNum);



        if (organisationModel.isPresent() && catalogModel.isPresent()) {
            OrganisationDataCatalogBuilder.OrganisationDataCatalogModelBuilder catalog = OrganisationDataCatalogBuilder.builder().dataCatalog()
                    .organisation(organisationModel.get())
                    .catalog(catalogModel.get());

            datasetModels.forEach(ds -> {
                catalog.dataset(ds);
            });
            return Optional.of(catalog.build());
        }

        return Optional.empty();
    }

    private Optional<Model> getOrganisationModel(String orgNum) {
        List<Organisation> organisations = organisationService.getOrganisations();

        Optional<Organisation> organisation = organisations.stream().filter(o -> o.getOrgNumber().equals(orgNum)).findFirst();

        return Optional.ofNullable(
                OrganisationBuilder.builder()
                        .organisation(organisation.get().getOrgNumber(), organisation.get().getDisplayName())
                        .build());
    }

    private Optional<Model> getCatalogModel(String orgNum) {
        List<Organisation> organisations = organisationService.getOrganisations();

        Optional<Organisation> organisation = organisations.stream().filter(o -> o.getOrgNumber().equals(orgNum)).findFirst();

        return Optional.ofNullable(
                CatalogBuilder.builder()
                        .organisation(organisation.get().getOrgNumber(), organisation.get().getDisplayName()).build());
    }

    private Optional<Organisation> getOrganisation(String orgNum) {
        List<Organisation> organisations = organisationService.getOrganisations();

        return organisations.stream().filter(o -> o.getOrgNumber().equals(orgNum)).findFirst();
    }

    private List<Model> getDatasetModels(String orgNum) {
        Optional<Organisation> organisation = getOrganisation(orgNum);
        List<Model> datasets = new ArrayList<>();

        if (organisation.isPresent()) {
            organisation.get().getComponents().forEach(componentDn -> {
                Optional<Component> component = componentService.getComponetByDn(componentDn);
                if (component.isPresent()) {
                    Component c = component.get();
                    datasets.add(DatasetBuilder.builder()
                            .organisation(orgNum, c.getName())
                            .title(c.getDescription())
                            .description(c.getDescription())
                            .type("Data")
                            .theme(EuMetadataRegistry.DataTheme.GOVE)
                            .accrualPeriodicity(EuMetadataRegistry.Frequency.CONT)
                            .spatial("https://data.geonorge.no/administrativeEnheter/fylke/doc/173152")
                            .build());
                }
            });
        }
        return datasets;
    }

    /*
    DatasetBuilder.builder()
                .organisation(orgNum, "2")
                .title("Dataset2")
                .description("Dataset2 description")
                .theme(EuMetadataRegistry.DataTheme.ECON)
                .theme(EuMetadataRegistry.DataTheme.GOVE)
                .type("Data")
                .objective("Used for testing")
                .source("The universe")
                .accessRights(EuMetadataRegistry.AccessRight.NON_PUBLIC)
                .accrualPeriodicity(EuMetadataRegistry.Frequency.CONT)
                .spatial("https://data.geonorge.no/administrativeEnheter/fylke/doc/173156")
                .keyword("dog")
                .keyword("cat")
                .provenance("Vedtak")
                .legalBasisForRestriction(LegalBasis.of("lov", "lovtittel"))
                .legalBasisForAccess(LegalBasis.of("lov", "lovtittel"))
                .legalBasisForProcessing(LegalBasis.of("lov", "lovtittel"))
                .completeness("Always complete")
                .accuracy("Always accurat")
                .currentness("Always current")
                .build()
     */


}
