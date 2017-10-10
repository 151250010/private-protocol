package codec;

import org.jboss.marshalling.*;

import java.io.IOException;

public class MarshallingFactory {

    public static Marshaller buildMarshalling() throws IOException {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return factory.createMarshaller(configuration);
    }

    public static Unmarshaller buildUnMarshalling() throws Exception {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return factory.createUnmarshaller(configuration);
    }
}
