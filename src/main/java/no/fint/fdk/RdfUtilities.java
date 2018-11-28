package no.fint.fdk;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.ByteArrayOutputStream;

public enum RdfUtilities {

    ;

    public static String getTurtleString(Model model) {
        return getModelString(model, Lang.TURTLE);
    }

    public static String getXmlString(Model model) {
        return getModelString(model, Lang.RDFXML);
    }

    public static String getJsonString(Model model) {
        return getModelString(model, Lang.RDFJSON);
    }

    public static String getModelString(Model model, Lang lang) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, lang.getName());

        return os.toString();
    }
}

