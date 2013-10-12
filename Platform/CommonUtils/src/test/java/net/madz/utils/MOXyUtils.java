package net.madz.utils;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.eclipsesource.restfuse.Response;

public class MOXyUtils {

    public static <T> T unmarshal(Response response2, Class<T> clz, Class<?>[] classesToBeBound) throws JAXBException,
            PropertyException {
        JAXBContext jc = org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(classesToBeBound, null);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty("eclipselink.media-type", "application/json");
        unmarshaller.setProperty("eclipselink.json.include-root", false);
        StreamSource source = new StreamSource(new StringReader(response2.getBody()));
        JAXBElement<T> sot = unmarshaller.unmarshal(source, clz);
        T value = sot.getValue();
        return value;
    }

    private MOXyUtils() {
    }
}
