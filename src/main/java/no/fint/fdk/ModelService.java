package no.fint.fdk;

import no.fint.fdk.vocabulary.EuMetadataRegistry;
import no.fint.portal.model.component.Component;
import no.fint.portal.model.component.ComponentService;
import no.fint.portal.model.organisation.Organisation;
import no.fint.portal.model.organisation.OrganisationService;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ModelService {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ComponentService componentService;

    public Model getFdkModel(Organisation organisation) {
        Model organisationModel = getOrganisationModel(organisation);
        Model catalogModel = getCatalogModel(organisation);
        Map<String, Model> datasetModels = getDatasetModels(organisation);


        OrganisationDataCatalogBuilder.OrganisationDataCatalogModelBuilder catalog = OrganisationDataCatalogBuilder.builder().dataCatalog()
                .organisation(organisationModel)
                .catalog(catalogModel);

        datasetModels.forEach((id, model) -> catalog.dataset(model));

        return catalog.build();
    }

    public Model getOrganisationModel(Organisation organisation) {

        return OrganisationBuilder.builder()
                .organisation(organisation.getOrgNumber(), organisation.getDisplayName())
                .build();

    }

    public Model getCatalogModel(Organisation organisation) {

        Map<String, Model> datasetModels = getDatasetModels(organisation);

        CatalogBuilder.CatalogResourceBuilder catalog = CatalogBuilder.builder()
                .organisation(organisation.getOrgNumber(), organisation.getDisplayName());

        datasetModels.forEach((id, model) -> catalog.dataset(id));

        return catalog.build();
    }

    public Optional<Organisation> getOrganisation(String orgNum) {
        List<Organisation> organisations = organisationService.getOrganisations();

        return organisations.stream().filter(o -> o.getOrgNumber().equals(orgNum)).findFirst();
    }

    public Optional<Model> getDatasetModel(Organisation organisation, String datasetId) {
        Map<String, Model> datasetModels = getDatasetModels(organisation);

        Optional<Map.Entry<String, Model>> ds = datasetModels.entrySet().stream().filter(entry -> entry.getKey().endsWith(datasetId)).findFirst();

        return ds.map(Map.Entry::getValue);

    }

    public Map<String, Model> getDatasetModels(Organisation organisation) {

        Map<String, Model> datasets = new HashMap<>();

        organisation.getComponents().forEach(componentDn -> {
            Optional<Component> component = componentService.getComponetByDn(componentDn);
            if (component.isPresent()) {
                Component c = component.get();
                datasets.put(Utilities.getDatasetResourceURI(organisation.getOrgNumber(), c.getName()), DatasetBuilder.builder()
                        .organisation(organisation.getOrgNumber(), c.getName())
                        .title(c.getDescription())
                        .description(c.getDescription())
                        .type("Data")
                        .theme(EuMetadataRegistry.DataTheme.GOVE)
                        .accrualPeriodicity(EuMetadataRegistry.Frequency.CONT)
                        .spatial("https://data.geonorge.no/administrativeEnheter/fylke/id/173152")
                        .build());
            }
        });
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
